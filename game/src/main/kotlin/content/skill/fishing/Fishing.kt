package content.skill.fishing

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.bank.bank
import net.pearx.kasechange.toLowerSpaceCase
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.data.Catch
import world.gregs.voidps.engine.data.definition.data.Spot
import world.gregs.voidps.engine.data.definition.data.Uncooked
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.success
import world.gregs.voidps.engine.event.Script
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.holdsItem
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove

@Script
class Fishing {

    val logger = InlineLogger()
    val itemDefinitions: ItemDefinitions by inject()

    val NPC.spot: Map<String, Spot>
        get() = def["fishing", emptyMap()]

    val Spot.minimumLevel: Int
        get() = bait.keys.minOf { minimumLevel(it) ?: Int.MAX_VALUE }

    init {
        npcOperate("*", "fishing_spot_*") {
            val playerName = player.name
            arriveDelay()
            if (!def.contains("fishing")) return@npcOperate

            target.getOrPut("fishers") { mutableSetOf<String>() }.add(playerName)
            player.softTimers.start("fishing")
            player.closeDialogue()
            val tile = target.tile
            var first = true

            fishing@ while (true) {
                if (player.inventory.isFull()) {
                    player.message("Your inventory is too full to hold any more fish.")
                    break
                }

                if (target.tile != tile) break

                val data = target.spot[option] ?: return@npcOperate
                if (!player.has(Skill.Fishing, data.minimumLevel, true)) break

                val isMasterTool = player.holdsItem("fishing_master_tool") || player.holdsItem("noose_wand")

                // Tackle check: master tool ignores required tackle
                val tackle = data.tackle.firstOrNull { player.holdsItem(it) } ?: if (isMasterTool) "master_tool" else null
                if (tackle == null) {
                    player.message("You need a ${data.tackle.first().toTitleCase()} to catch these fish.")
                    break@fishing
                }

                // Select catches
                val catches: List<String> = if (isMasterTool) {
                    // Noose Wand or Fishing Master Tool: ignore bait, grab all fish from spot
                    data.bait.values.flatten()
                } else {
                    val bait = data.bait.keys.firstOrNull { baitKey -> baitKey == "none" || player.holdsItem(baitKey) }
                        ?: run {
                            player.message("You don't have any ${data.bait.keys.first().toTitleCase().plural(2)}.")
                            break@fishing
                        }
                    data.bait[bait] ?: run {
                        player.message("You don't have any ${data.bait.keys.first().toTitleCase().plural(2)}.")
                        break@fishing
                    }
                }

                if (first) {
                    player.message(itemDefinitions.get(tackle)["cast", ""], ChatType.Filter)
                    first = false
                }

                val remaining = player.remaining("action_delay")
                if (remaining < 0) {
                    player.face(target)

                    // Animation: harpoon if using Noose Wand, rod otherwise
                    val anim = when {
                        tackle == "fishing_rod" || tackle == "fly_fishing_rod" || tackle == "barbarian_rod" -> "fish_${if (first) "fishing_rod" else "rod"}"
                        player.holdsItem("noose_wand") -> "fish_harpoon"
                        else -> "fish_$tackle"
                    }
                    player.anim(anim)
                    pause(1)
                } else if (remaining > 0) {
                    return@npcOperate
                }

                for (item in catches) {
                    val catch = itemDefinitions.get(item)["fishing", Catch.EMPTY]
                    val level = player.levels.get(Skill.Fishing)
                    if (level >= catch.level && success(level, catch.chance)) {
                        // Skip bait removal if using master tool
                        if (!isMasterTool && !player.inventory.remove(item)) {
                            break@fishing
                        }

                        if (player.holdsItem("noose_wand")) {
                            player.message("Your Noose Wand lets you fish anywhere without bait!", ChatType.Filter)
                        }

                        addCatch(player, item)
                        break
                    }
                }
                player.stop("action_delay")
            }

            target.get<MutableSet<String>>("fishers")?.remove(playerName)
            player.softTimers.stop("fishing")
        }
    }

    fun addCatch(player: Player, catchId: String) {
        val def = itemDefinitions.getOrNull(catchId)
        val cooking = def?.getOrNull<Uncooked>("cooking")
        val catchDef = itemDefinitions.get(catchId)["fishing", Catch.EMPTY]

        val doubleCatch = Math.random() < 0.5

        fun addFish(fish: String, xp: Double, skill: Skill) {
            if (player.bank.add(fish)) {
                val total = player.bank.count(fish)
                player.message(
                    "You caught ${fish.toLowerSpaceCase()}! It was sent to your bank. You now have $total stored.",
                    ChatType.Filter,
                )
                player.experience.add(skill, xp)
            } else {
                player.message("Your bank is full! You lose the fish.", ChatType.Filter)
            }
        }

        // Auto-cook
        if (cooking != null && Math.random() < 0.5) {
            val cookedId = cooking.cooked.ifEmpty { catchId.replace("raw_", "cooked_") }
            addFish(cookedId, cooking.xp, Skill.Cooking)
            if (doubleCatch) {
                addFish(cookedId, cooking.xp, Skill.Cooking)
                player.message("Amazing! You caught double ${cookedId.toLowerSpaceCase()}!", ChatType.Filter)
            }
            return
        }

        // Raw fish
        addFish(catchId, catchDef.xp, Skill.Fishing)
        if (doubleCatch) {
            addFish(catchId, catchDef.xp, Skill.Fishing)
            player.message("Lucky! You caught double ${catchId.toLowerSpaceCase()}!", ChatType.Filter)
        }
    }

    fun Spot.minimumLevel(bait: String): Int? = this.bait[bait]?.minOf { itemDefinitions.get(it)["fishing", Catch.EMPTY].level }
}

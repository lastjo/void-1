package content.skill.fishing

import com.github.michaelbull.logging.InlineLogger
import net.pearx.kasechange.toLowerSpaceCase
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.data.Catch
import world.gregs.voidps.engine.data.definition.data.Spot
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.success
import world.gregs.voidps.engine.event.Script
import world.gregs.voidps.engine.event.on
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.holdsItem
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.type.random

@Script
class Fishing {

    private val logger = InlineLogger()
    private val itemDefinitions: ItemDefinitions by inject()

    /** Fishing data on NPC definitions */
    val NPC.spot: Map<String, Spot>
        get() = def["fishing", emptyMap()]

    val Spot.minimumLevel: Int
        get() = bait.keys.minOf { minimumLevelForBait(it) ?: Int.MAX_VALUE }

    init {
        on<NPCOption> {

            // Only fishing spots
            if (!target.def.contains("fishing")) {
                return@on
            }

            val data = target.spot[option] ?: return@on

            if (!player.has(Skill.Fishing, data.minimumLevel, message = true)) {
                return@on
            }

            if (player.inventory.isFull()) {
                player.message("Your inventory is too full to hold any more fish.")
                return@on
            }

            player.closeDialogue()
            player.face(target)

            val tackle = data.tackle.firstOrNull(player::holdsItem)
            if (tackle == null) {
                player.message(
                    "You need a ${data.tackle.first().toTitleCase()} to catch these fish."
                )
                return@on
            }

            val bait = data.bait.keys.firstOrNull { it == "none" || player.holdsItem(it) }
            val catches = data.bait[bait]
            if (bait == null || catches == null) {
                player.message(
                    "You don't have any ${data.bait.keys.first().toTitleCase().plural(2)}."
                )
                return@on
            }

            player.message(itemDefinitions.get(tackle)["cast", ""], ChatType.Filter)

            for (item in catches) {
                val catch = itemDefinitions.get(item)["fishing", Catch.EMPTY]
                val level = player.levels.get(Skill.Fishing)

                if (level >= catch.level && success(level, catch.chance)) {
                    if (bait != "none" && !player.inventory.remove(bait)) {
                        return@on
                    }
                    player.experience.add(Skill.Fishing, catch.xp)
                    addCatch(player, item)
                    break
                }
            }
        }
    }

    private fun addCatch(player: Player, catch: String) {
        var fish = catch
        var message = "You catch some ${fish.toLowerSpaceCase()}."

        if (bigCatch(fish)) {
            fish = fish.replace("raw_", "big_")
            message = "You catch an enormous ${catch.toLowerSpaceCase()}!"
        }

        val tx = player.inventory.add(fish)
        when (tx.error) {
            TransactionError.None -> player.message(message, ChatType.Filter)
            is TransactionError.Full -> player.inventoryFull()
            else -> logger.warn { "Error adding fish $fish ${tx.error}" }
        }
    }

    private fun bigCatch(catch: String): Boolean = when {
        World.members -> false
        catch == "raw_bass" && random.nextInt(1000) == 0 -> true
        catch == "raw_swordfish" && random.nextInt(2500) == 0 -> true
        catch == "raw_shark" && random.nextInt(5000) == 0 -> true
        else -> false
    }

    private fun Spot.minimumLevelForBait(bait: String): Int? =
        this.bait[bait]?.minOf {
            itemDefinitions.get(it)["fishing", Catch.EMPTY].level
        }
}

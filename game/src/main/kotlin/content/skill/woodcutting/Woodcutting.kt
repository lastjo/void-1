package content.skill.woodcutting

import content.entity.player.bank.bank
import content.entity.sound.areaSound
import net.pearx.kasechange.toLowerSpaceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.data.definition.ObjectDefinitions
import world.gregs.voidps.engine.data.definition.data.Fire
import world.gregs.voidps.engine.data.definition.data.Tree
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Interpolation
import world.gregs.voidps.engine.entity.character.player.skill.level.Level
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.drop.DropTables
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.event.Script
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.map.collision.random
import world.gregs.voidps.engine.suspend.awaitDialogues
import world.gregs.voidps.type.random

@Script
class Woodcutting {

    val players: Players by inject()
    val definitions: ObjectDefinitions by inject()
    val objects: GameObjects by inject()
    val floorItems: FloorItems by inject()
    val drops: DropTables by inject()

    val minPlayers = 0
    val maxPlayers = 2000

    init {
        objectOperate("Chop*") {
            val tree: Tree = def.getOrNull("woodcutting") ?: return@objectOperate
            val hatchet = Hatchet.best(player)
            if (hatchet == null) {
                player.message("You need a hatchet to chop down this tree.")
                player.message("You do not have a hatchet which you have the woodcutting level to use.")
                return@objectOperate
            }
            player.closeDialogue()
            player.softTimers.start("woodcutting")
            val ivy = tree.log.isEmpty()
            var first = true
            while (awaitDialogues()) {
                if (!objects.contains(target) || !player.has(Skill.Woodcutting, tree.level, true)) {
                    break
                }

                if (!ivy && player.inventory.isFull()) {
                    player.message("Your inventory is too full to hold any more logs.")
                    break
                }

                if (!Hatchet.hasRequirements(player, hatchet, true)) {
                    break
                }
                if (first) {
                    player.message("You swing your hatchet at the ${if (ivy) "ivy" else "tree"}.")
                    first = false
                }

                val remaining = player.remaining("action_delay")
                if (remaining < 0) {
                    player.anim("${hatchet.id}_chop${if (ivy) "_ivy" else ""}")
                    player.start("action_delay", 0)
                    pause(0)
                } else if (remaining > 0) {
                    pause(remaining)
                }

                if (!objects.contains(target)) {
                    break
                }

                if (success(player.levels.get(Skill.Woodcutting), hatchet, tree)) {
                    tryDropNest(player, ivy)
                    if (!addLog(player, tree) || deplete(tree, target)) {
                        break
                    }
                    if (ivy) {
                        player.message("You successfully chop away some ivy.")
                    }
                }

                player.stop("action_delay")
            }
            player.softTimers.stop("woodcutting")
        }
    }

    fun tryDropNest(player: Player, ivy: Boolean) {
        val dropChance = 4
        if (random.nextInt(dropChance) != 0) return
        val table = drops.get("birds_nest_table") ?: return

        val hasRabbitFoot = player.equipment.contains("strung_rabbit_foot")
        val totalWeight = if (hasRabbitFoot) 95 else 100

        val drop = table.role(totalWeight).firstOrNull() ?: return
        val source = if (ivy) "ivy" else "tree"

        // Add nest directly to the bank
        val amount = drop.amount?.start ?: 1
        val added = player.bank.add(drop.id, amount = amount)
        if (added) {
            val current = player["birds_nests_received", 0] + amount
            player["birds_nests_received"] = current
            player.message("<red>A bird's nest falls out of the $source! (Total nests in bank: $current)")
            areaSound("bird_chirp", player.tile)
        } else {
            player.inventoryFull()
            val dropTile = player.tile.toCuboid(1).random(player) ?: player.tile
            floorItems.add(tile = dropTile, id = drop.id, amount = amount, disappearTicks = 500)
            player.message("<red>A bird's nest falls out of the $source, but your bank is full!")
        }
    }

    fun success(level: Int, hatchet: Item, tree: Tree): Boolean {
        val lowHatchetChance = calculateChance(hatchet, tree.hatchetLowDifference)
        val highHatchetChance = calculateChance(hatchet, tree.hatchetHighDifference)
        val chance = tree.chance.first + lowHatchetChance..tree.chance.last + highHatchetChance
        return Level.success(level, chance)
    }

    fun calculateChance(hatchet: Item, treeHatchetDifferences: IntRange): Int = (0 until hatchet.def["rank", 0]).sumOf { calculateHatchetChance(it, treeHatchetDifferences) }

    fun calculateHatchetChance(hatchet: Int, treeHatchetDifferences: IntRange): Int = if (hatchet % 4 < 2) treeHatchetDifferences.last else treeHatchetDifferences.first

    fun addLog(player: Player, tree: Tree): Boolean {
        val log = tree.log
        if (log.isEmpty()) return true

        val amount = if (random.nextBoolean()) 2 else 1
        val added = player.bank.add(log, amount = amount)
        if (added) {
            // Woodcutting XP
            player.experience.add(Skill.Woodcutting, tree.xp * amount)

            // 50% chance to gain Firemaking XP
            if (random.nextBoolean()) {
                val fire: Fire? = Item(log).def.getOrNull("firemaking")
                if (fire != null) {
                    player.exp(Skill.Firemaking, fire.xp * amount)
                    player.message("You gain ${fire.xp * amount} Firemaking XP from the logs.")
                }
            }

            // Track total logs
            val current = player["logs_chopped_$log", 0] + amount
            player["logs_chopped_$log"] = current

            // Show message
            player.message("You get $amount ${log.toLowerSpaceCase()}${if (amount > 1) "s" else ""}. (Total in bank: $current)")
        } else {
            player.inventoryFull()
        }
        return added
    }

    fun deplete(tree: Tree, obj: GameObject): Boolean {
        val depleted = random.nextDouble() <= tree.depleteRate
        if (!depleted) return false

        val stumpId = "${obj.id}_stump"
        if (definitions.contains(stumpId)) {
            val delay = getRegrowTickDelay(tree)
            objects.replace(obj, stumpId, ticks = delay)
            areaSound("fell_tree", obj.tile)
        }
        return true
    }

    fun getRegrowTickDelay(tree: Tree): Int {
        val delay = tree.respawnDelay
        return if (tree.level == 1) {
            random.nextInt(delay.first, delay.last)
        } else {
            Interpolation.interpolate(players.size, delay.last, delay.first, minPlayers, maxPlayers)
        }
    }
}

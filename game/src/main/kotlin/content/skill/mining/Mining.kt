package content.skill.mining

import content.activity.shooting_star.ShootingStarHandler
import content.entity.player.bank.bank
import net.pearx.kasechange.toLowerSpaceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.data.Ore
import world.gregs.voidps.engine.data.definition.data.Rock
import world.gregs.voidps.engine.data.definition.data.Smelting
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.hasRequirementsToUse
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.success
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.entity.obj.objectApproach
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.event.Script
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.random

@Script
class Mining {

    val objects: GameObjects by inject()
    val itemDefinitions: ItemDefinitions by inject()

    val gems = setOf(
        "uncut_sapphire",
        "uncut_emerald",
        "uncut_ruby",
        "uncut_diamond",
        "uncut_onyx",
    )

    init {
        objectOperate("Mine") {
            if (target.id.startsWith("depleted")) {
                player.message("There is currently no ore available in this rock.")
                return@objectOperate
            }

            player.softTimers.start("mining")
            var first = true

            while (true) {
                if (!objects.contains(target)) break
                if (player.inventory.isFull()) {
                    player.message("Your inventory is too full to hold any more ore.")
                    break
                }

                val rock: Rock? = target.def.getOrNull("mining")
                if (rock == null || !player.has(Skill.Mining, rock.level, true)) break

                val pickaxe: Item = Pickaxe.best(player) ?: break
                if (!hasRequirements(player, pickaxe, true)) break

                if (first) {
                    player.message("You swing your pickaxe at the rock.", ChatType.Filter)
                    first = false
                }

                val remaining = player.remaining("action_delay")
                val delay = if (pickaxe.id == "dragon_pickaxe" && random.nextInt(6) == 0) 2 else pickaxe.def["mining_delay", 0]

                if (remaining < 0) {
                    player.face(target)
                    player.anim("${pickaxe.id}_swing_low")
                    player.start("action_delay", delay)
                    pause(delay)
                } else if (remaining > 0) {
                    pause(delay)
                }
                if (!objects.contains(target)) break

                // Gems chance
                if (rock.gems) {
                    val glory = player.equipped(EquipSlot.Amulet)?.id?.startsWith("amulet_of_glory_") == true
                    if (success(player.levels.get(Skill.Mining), if (glory) 3..3 else 1..1)) {
                        addOre(player, gems.random(), xp = 0.0)
                        continue
                    }
                }

                // Rune essence handling
                var ores = rock.ores
                if (target.id == "rune_essence_rocks") {
                    ores = rock.ores.filter { if (World.members && player.has(Skill.Mining, 30)) it == "pure_essence" else it == "rune_essence" }
                }

                for (item in ores) {
                    val ore = itemDefinitions.get(item)["mining", Ore.EMPTY]
                    if (success(player.levels.get(Skill.Mining), ore.chance)) {
                        // XP per ore mined
                        val mined = addOre(player, item, xp = ore.xp)
                        if (!mined || deplete(rock, target)) {
                            player.clearAnim()
                            break
                        }
                    }
                }
                player.stop("action_delay")
            }

            player.softTimers.stop("mining")
        }

        objectApproach("Prospect") {
            approachRange(1)
            arriveDelay()
            if (target.id.startsWith("depleted")) {
                player.message("There is currently no ore available in this rock.")
                return@objectApproach
            }
            if (player.queue.contains("prospect")) return@objectApproach

            player.message("You examine the rock for ores...")
            pause(4)

            val ore = def.getOrNull<Rock>("mining")?.ores?.firstOrNull()
            if (ore == null) {
                player.message("This rock contains no ore.")
            } else {
                player.message("This rock contains ${ore.toLowerSpaceCase()}.")
            }
        }
    }

    fun hasRequirements(player: Player, pickaxe: Item, message: Boolean = false): Boolean = player.hasRequirementsToUse(pickaxe, message, setOf(Skill.Mining, Skill.Firemaking))

    fun addOre(player: Player, ore: String, xp: Double = 0.0): Boolean {
        // Stardust
        if (ore == "stardust") {
            ShootingStarHandler.addStarDustCollected()
            val totalStarDust = player.inventory.count(ore) + player.bank.count(ore)
            if (totalStarDust >= 200) {
                player.message("You have the maximum amount of stardust but were still rewarded experience.")
                return true
            }
        }

        // Clay -> soft clay
        if (ore == "clay" && random.nextInt(2) == 0) {
            val added = player.bank.add("soft_clay")
            if (added && xp > 0) player.experience.add(Skill.Mining, xp)
            sendBankTracker(player, "soft_clay")
            return added
        }

        // Gems
        if (gems.contains(ore)) {
            val added = player.bank.add(ore)
            if (added && xp > 0) player.experience.add(Skill.Mining, xp)
            sendBankTracker(player, ore)
            return added
        }

        // Iron -> 50% chance steel bar, coal optional
        if (ore == "iron_ore" && random.nextInt(2) == 0) {
            if (player.bank.count("coal") > 0) {
                player.bank.remove("coal", 1)
            } else if (player.inventory.count("coal") > 0) {
                player.inventory.remove("coal", 1)
            }

            val steelBarAdded = player.bank.add("steel_bar")
            if (steelBarAdded) {
                if (xp > 0) player.experience.add(Skill.Mining, xp)
                val steelXp = itemDefinitions.get("steel_bar")["smelting", Smelting.EMPTY].xp
                player.experience.add(Skill.Smithing, steelXp)
                sendBankTracker(player, "steel_bar")
                return true
            }
        }

        // 50% chance double ore
        if (random.nextInt(2) == 0) {
            val added = player.bank.add(ore, amount = 2)
            if (added && xp > 0) player.experience.add(Skill.Mining, xp * 2)
            sendBankTracker(player, ore)
            return added
        }

        // 50% chance normal smelt
        val smelted = trySmeltOre(player, ore)
        if (smelted != null) {
            val (bar, smeltXp) = smelted
            val added = player.bank.add(bar)
            if (added) {
                player.experience.add(Skill.Smithing, smeltXp)
                if (xp > 0) player.experience.add(Skill.Mining, xp)
                sendBankTracker(player, bar)
            }
            return added
        }

        // Normal ore add
        val added = player.bank.add(ore)
        if (added && xp > 0) {
            player.experience.add(Skill.Mining, xp)
        } else if (!added) {
            player.inventoryFull()
        }
        sendBankTracker(player, ore)
        return added
    }

    private fun sendBankTracker(player: Player, item: String) {
        val total = player.bank.count(item)
        player.message("You now have $total ${item.toLowerSpaceCase()}(s) in your bank.", ChatType.Game)
    }

    fun trySmeltOre(player: Player, ore: String): Pair<String, Double>? {
        val bar = when (ore) {
            "copper_ore", "tin_ore" -> "bronze_bar"
            "iron_ore" -> "iron_bar"
            "silver_ore" -> "silver_bar"
            "gold_ore" -> "gold_bar"
            "mithril_ore" -> "mithril_bar"
            "adamantite_ore" -> "adamant_bar"
            "runite_ore" -> "rune_bar"
            else -> null
        }
        if (bar != null) {
            val smelting = itemDefinitions.get(bar)["smelting"] as? Smelting
            val xp = smelting?.xp ?: 0.0
            return bar to xp
        }
        return null
    }

    fun deplete(rock: Rock, obj: GameObject): Boolean {
        if (obj.id.startsWith("crashed_star_tier_")) {
            ShootingStarHandler.handleMinedStarDust(obj)
            return false
        }
        if (rock.life >= 0) {
            objects.replace(obj, "depleted${obj.id.dropWhile { it != '_' }}", ticks = rock.life)
            return true
        }
        return false
    }
}

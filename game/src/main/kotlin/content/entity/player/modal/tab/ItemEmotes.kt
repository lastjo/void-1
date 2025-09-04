package content.entity.player.modal.tab

import content.entity.effect.toxin.curePoison
import content.entity.effect.toxin.poisoned
import content.entity.sound.jingle
import content.entity.sound.sound
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.ui.menu
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.queue.queue
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Continue
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.Option

class ItemEmotes {

    @Inventory("Fly", "toy_kite")
    suspend fun flyKite(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_fly_kite")
    }

    @Inventory("Emote", "reindeer_hat", "worn_equipment")
    suspend fun reindeerHat(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("emote_reindeer")
        player.gfx("emote_reindeer_2")
        player.animDelay("emote_reindeer")
    }

    @Inventory("Recite-prayer", "prayer_book", "inventory")
    suspend fun prayerBook(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        if (player.poisoned) {
            val poisonDamage: Int = player["poison_damage"] ?: return
            var points = (poisonDamage - 20) / 2
            var decrease = poisonDamage
            val prayer = player.levels.get(Skill.Prayer)
            if (points > prayer) {
                decrease = (prayer * 2) + 2
                points = prayer
            }
            if (points > 0) {
                player.levels.drain(Skill.Prayer, points)
                player["poison_damage"] = poisonDamage - decrease
                if (poisonDamage - decrease <= 10) {
                    player.curePoison()
                }
            }
        }
        player.animDelay("emote_recite_prayer")
    }

    @Option("Whack")
    suspend fun whack(player: Player, target: Player) {
        if (player.weapon.id == "rubber_chicken") {
            player.sound("rubber_chicken_whack")
            player.animDelay("rubber_chicken_whack")
        } else {
            // todo player.playSound("")
            player.animDelay("easter_carrot_whack")
        }
    }

    @Inventory("Dance", "rubber_chicken")
    suspend fun chickenDance(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.jingle("easter_scape_scrambled")
        player.animDelay("emote_chicken_dance")
    }

    @Inventory("Spin", "spinning_plate", "inventory")
    suspend fun spinningPlate(player: Player, item: Item) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        val drop = random.nextBoolean()
        player.animDelay("emote_spinning_plate")
        player.animDelay("emote_spinning_plate_${if (drop) "drop" else "take"}")
        player.animDelay("emote_${if (drop) "cry" else "cheer"}")
    }

    @Continue("snow_globe", "continue")
    fun close(player: Player) {
        player.close("snow_globe")
    }

    @Close("snow_globe")
    fun snowGlobe(player: Player) {
        player.queue("snow_globe_close") {
            player.gfx("emote_snow_globe_flurry")
            val ticks = player.anim("emote_trample_snow")
            pause(ticks)
            player.message("The snow globe fills your inventory with snow!")
            player.inventory.add("snowball_2007_christmas_event", player.inventory.spaces)
            player.clearAnim()
            player.closeDialogue()
        }
    }

    @Inventory("Shake", "snow_globe", "inventory")
    fun shake(player: Player) {
        if (player.contains("delay") || player.menu != null) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.queue("snow_globe") {
            player.message("You shake the snow globe.")
            player.animDelay("emote_shake_snow_globe")
            player.jingle("harmony_snow_globe")
            player.open("snow_globe")
        }
    }

    @Inventory("Play", "yo_yo")
    @Inventory("Loop", "yo_yo")
    @Inventory("Walk", "yo_yo")
    @Inventory("Crazy", "yo_yo")
    suspend fun yoyo(player: Player, option: String) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_yoyo_${option.lowercase()}")
    }

    @Inventory("Spin", "candy_cane", "worn_equipment")
    suspend fun candyCane(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_candy_cane_spin")
    }

    @Inventory("Dance", "salty_claws_hat", "worn_equipment")
    suspend fun dance(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_salty_claws_hat_dance")
    }

    @Inventory("Celebrate", "tenth_anniversary_cake")
    suspend fun celebrate(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("10th_anniversary_cake")
        player.animDelay("emote_10th_anniversary_cake")
    }

    @Inventory("Brandish (2009)", "golden_hammer", "worn_equipment")
    suspend fun brandish(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_golden_hammer_brandish")
    }

    @Inventory("Spin (2010)", "golden_hammer", "worn_equipment")
    suspend fun spinGoldenHammer(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("emote_golden_hammer_spin")
        player.animDelay("emote_golden_hammer_spin")
    }

    @Inventory("Jump", "*_marionette")
    @Inventory("Walk", "*_marionette")
    @Inventory("Bow", "*_marionette")
    @Inventory("Dance", "*_marionette")
    suspend fun marionette(player: Player, item: Item, option: String) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("emote_${item.id}_${option.lowercase()}")
        player.animDelay("emote_marionette_${option.lowercase()}")
    }

    @Inventory("Sleuth", "magnifying_glass", "worn_equipment")
    suspend fun magnifyingGlass(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.animDelay("emote_magnifying_glass_sleuth")
    }

    @Inventory("Emote", "chocatrice_cape", "worn_equipment")
    suspend fun chocatriceCape(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("emote_chocatrice_cape")
        player.animDelay("emote_chocatrice_cape")
    }

    @Inventory("Juggle", "squirrel_ears", "worn_equipment")
    suspend fun squirrelEars(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("emote_squirrel_ears")
        player.animDelay("emote_squirrel_ears")
    }

    @Inventory("Play-with", "toy_horsey_*")
    suspend fun toyHorse(player: Player, item: Item) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.say(
            when (random.nextInt(0, 3)) {
                0 -> "Come on Dobbin, we can win the race!"
                1 -> "Hi-ho Silver, and away!"
                else -> "Neaahhhyyy! Giddy-up horsey!"
            },
        )
        //    player.say("Just say neigh to gambling!")
        player.animDelay("emote_${item.id}")
    }

    @Inventory("Play-with", "eek")
    suspend fun eek(player: Player) {
        if (player.contains("delay")) {
            player.message("Please wait till you've finished performing your current emote.")
            return
        }
        player.gfx("play_with_eek")
        player.animDelay("play_with_eek")
    }

    @Inventory("Summon Minion", "squirrel_ears", "worn_equipment")
    fun squirrel(player: Player) {
        // todo summon npc 9682 and 9681 if dismiss have to wait 30mins before able to summon again
    }
}

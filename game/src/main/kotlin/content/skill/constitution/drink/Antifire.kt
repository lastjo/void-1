package content.skill.constitution.drink

import content.entity.player.effect.antifire
import content.entity.player.effect.superAntifire
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

class Antifire {

    @Spawn
    fun spawn(player: Player) {
        if (player.antifire) {
            player.timers.restart("fire_resistance")
        }
        if (player.superAntifire) {
            player.timers.restart("fire_immunity")
        }
    }

    @TimerStart("fire_resistance")
    fun resistance(player: Player): Int = 30

    @TimerStart("fire_immunity")
    fun immunity(player: Player): Int = 20

    @TimerTick("fire_resistance", "fire_immunity")
    fun tick(player: Player, timer: String): Int {
        val remaining = player.dec(if (timer == "fire_immunity") "super_antifire" else "antifire", 0)
        if (remaining <= 0) {
            return TimerState.CANCEL
        }
        if (remaining == 1) {
            player.message("<dark_red>Your resistance to dragonfire is about to run out.")
        }
        return TimerState.CONTINUE
    }

    @TimerStop("fire_resistance", "fire_immunity")
    fun stop(player: Player) {
        player.message("<dark_red>Your resistance to dragonfire has run out.")
        player["antifire"] = 0
        player["super_antifire"] = 0
    }
}

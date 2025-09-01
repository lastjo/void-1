package content.entity.player.combat.special

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.timer.Timer
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerTick
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import kotlin.math.min

class SpecialAttackEnergy {

    val half = MAX_SPECIAL_ATTACK / 2
    val tenth = MAX_SPECIAL_ATTACK / 10

    @Spawn
    fun spawn(player: Player) {
        if (player.specialAttackEnergy < MAX_SPECIAL_ATTACK) {
            player.softTimers.start("restore_special_energy")
        }
    }

    @TimerStart("restore_special_energy")
    fun start(player: Player): Int {
        return 50
    }

    @TimerTick("restore_special_energy")
    fun tick(player: Player): Int {
        val energy = player.specialAttackEnergy
        if (energy >= MAX_SPECIAL_ATTACK) {
            return 0
        }
        val restore = min(tenth, MAX_SPECIAL_ATTACK - energy)
        player.specialAttackEnergy += restore
        if (player.specialAttackEnergy.rem(half) == 0) {
            player.message("Your special attack energy is now ${if (player.specialAttackEnergy == MAX_SPECIAL_ATTACK) 100 else 50}%.")
        }
        return -1
    }

}

package content.skill.constitution.drink

import content.entity.player.combat.special.MAX_SPECIAL_ATTACK
import content.entity.player.combat.special.specialAttackEnergy
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.Consume
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

class RecoverSpecial {

    @Consume("recover_special*")
    fun drink(player: Player): Boolean {
        if (player.specialAttackEnergy == MAX_SPECIAL_ATTACK) {
            player.message("Drinking this would have no effect.")
            return true
        } else if (player.softTimers.contains("recover_special")) {
            player.message("You may only use this pot once every 30 seconds.")
            return true
        }
        return false
    }

    @TimerStart("recover_special")
    fun start(player: Player): Int = 10

    @TimerTick("recover_special")
    fun tick(player: Player): Int {
        if (player.dec("recover_special_delay") <= 0) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }

    @TimerStop("recover_special")
    fun stop(player: Player) {
        player.clear("recover_special_delay")
    }
}

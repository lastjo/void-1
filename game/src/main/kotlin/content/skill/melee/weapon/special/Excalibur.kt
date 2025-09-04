package content.skill.melee.weapon.special

import content.entity.player.combat.special.SpecialAttack
import content.entity.sound.sound
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class Excalibur {

    @world.gregs.voidps.type.sub.SpecialAttack("sanctuary")
    fun excalibur(player: Player, id: String): Boolean {
        if (!SpecialAttack.drain(player)) {
            return true
        }
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        player.say("For Camelot!")
        if (player.weapon.id.startsWith("enhanced")) {
            player.levels.boost(Skill.Defence, multiplier = 0.15)
            player[id] = TimeUnit.SECONDS.toTicks(if (seersVillageEliteTasks(player)) 24 else 12) / 4
            player.softTimers.start(id)
        } else {
            player.levels.boost(Skill.Defence, amount = 8)
        }
        return true
    }

    @TimerStart("sanctuary")
    fun start(player: Player): Int = 4

    @TimerTick("sanctuary")
    fun tick(player: Player): Int {
        val cycle = player["sanctuary", 1] - 1
        player["sanctuary"] = cycle
        if (cycle <= 0) {
            return TimerState.CANCEL
        }
        player.levels.restore(Skill.Constitution, 40)
        return TimerState.CONTINUE
    }

    @TimerStop("sanctuary")
    fun stop(player: Player) {
        player.clear("sanctuary")
    }

    fun seersVillageEliteTasks(player: Player) = false
}

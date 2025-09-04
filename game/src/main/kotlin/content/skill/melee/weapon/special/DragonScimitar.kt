package content.skill.melee.weapon.special

import content.skill.prayer.getActivePrayerVarKey
import content.skill.prayer.isCurses
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.PrayerStart
import world.gregs.voidps.type.sub.SpecialAttack
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class DragonScimitar {

    @SpecialAttack("sever", damage = true)
    fun dragonScimitar(player: Player, target: Character, damage: Int) {
        target.softTimers.start("sever")
    }

    @TimerStart("sever")
    fun start(player: Player): Int {
        val key = player.getActivePrayerVarKey()
        if (player.isCurses()) {
            player.removeVarbit(key, "deflect_magic")
            player.removeVarbit(key, "deflect_melee")
            player.removeVarbit(key, "deflect_missiles")
            player.removeVarbit(key, "deflect_summoning")
        } else {
            player.removeVarbit(key, "protect_from_magic")
            player.removeVarbit(key, "protect_from_melee")
            player.removeVarbit(key, "protect_from_missiles")
            player.removeVarbit(key, "protect_from_summoning")
        }
        return TimeUnit.SECONDS.toTicks(5)
    }

    @TimerTick("sever")
    fun tick(player: Player): Int = TimerState.CANCEL

    @PrayerStart("prayer_deflect_*", "prayer_protect_*")
    fun pray(player: Player, id: String) {
        if (!player.softTimers.contains("sever")) {
            return
        }
        player.message("You've been injured and can no longer use ${if (player.isCurses()) "deflect curses" else "protection prayers"}!")
        val key = player.getActivePrayerVarKey()
        player.removeVarbit(key, id.removePrefix("prayer_").toTitleCase())
    }
}

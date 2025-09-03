package content.skill.melee.weapon.special

import content.entity.combat.hit.combatDamage
import content.entity.player.combat.special.SpecialAttack
import content.entity.player.combat.special.specialAttackPrepare
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inv.itemRemoved
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerStop
import world.gregs.voidps.engine.timer.timerTick
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.*
import java.util.concurrent.TimeUnit

class StaffOfLight {

    @ItemRemoved("staff_of_light*", slots = [EquipSlot.WEAPON], inventory = "worn_equipment")
    fun removed(player: Player) {
        player.softTimers.stop("power_of_light")
    }

    @Combat(stage = CombatStage.DAMAGE)
    fun damage(player: Player, target: Character) {
        if (player.softTimers.contains("power_of_light")) {
            player.gfx("power_of_light_impact")
        }
    }

    @world.gregs.voidps.type.sub.SpecialAttack("power_of_light")
    fun prepare(player: Player, id: String): Boolean {
        if (!SpecialAttack.drain(player)) {
            return true
        }
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player[id] = TimeUnit.MINUTES.toTicks(1)
        player.softTimers.start(id)
        return true
    }

    @Spawn
    fun spawn(player: Player) {
        if (player.contains("power_of_light")) {
            player.softTimers.restart("power_of_light")
        }
    }

    @TimerStart("power_of_light")
    fun start(player: Player): Int {
        return 1
    }

    @TimerTick("power_of_light")
    fun tick(player: Player): Int {
        if (player.dec("power_of_light") <= 0) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }

    @TimerStop("power_of_light")
    fun stop(player: Player) {
        player.message("<red>The power of the light fades. Your resistance to melee attacks returns to normal.")
        player.clear("power_of_light")
    }

}

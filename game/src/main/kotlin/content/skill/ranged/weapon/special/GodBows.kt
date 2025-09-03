package content.skill.ranged.weapon.special

import content.entity.combat.hit.*
import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.timer.*
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class GodBows {

    var Player.restoration: Int
        get() = this["restoration", 0]
        set(value) {
            this["restoration"] = value
        }

    @Combat("saradomin_bow")
    @Combat("guthix_bow")
    @Combat("zamorak_bow")
    fun combat(player: Player, target: Character, weapon: Item, type: String, delay: Int, spell: String, special: Boolean, damage: Int) {
        if (!special) {
            return
        }
        when (weapon.id) {
            "zamorak_bow" -> target.hit(player, weapon, type, CLIENT_TICKS.toTicks(delay), spell, special, type, damage)
            "saradomin_bow" -> {
                player.restoration += damage * 2
                player["restoration_amount"] = player.restoration / 10
                player.softTimers.start("restorative_shot")
            }
            "guthix_bow" -> {
                player.restoration += (damage * 1.5).toInt()
                player["restoration_amount"] = player.restoration / 10
                player.softTimers.start("balanced_shot")
            }
        }
    }

    @Combat("saradomin_bow", stage = CombatStage.DAMAGE)
    @Combat("guthix_bow", stage = CombatStage.DAMAGE)
    @Combat("zamorak_bow", stage = CombatStage.DAMAGE)
    fun damage(player: Player, target: Character, weapon: Item, special: Boolean) {
        if (special) {
            player.gfx("${weapon.id}_special_impact")
            player.sound("god_bow_special_impact")
        }
    }

    @TimerStart("restorative_shot", "balanced_shot")
    fun start(player: Player): Int  = TimeUnit.SECONDS.toTicks(6)

    @TimerTick("restorative_shot", "balanced_shot")
    fun tick(player: Player): Int {
        val amount = player.restoration
        if (amount <= 0) {
            return TimerState.CANCEL
        }
        val restore = player["restoration_amount", 0]
        player.restoration -= restore
        player.levels.restore(Skill.Constitution, restore)
        player.gfx("saradomin_bow_restoration")
        return TimerState.CONTINUE
    }

    @TimerStop("restorative_shot", "balanced_shot")
    fun stop(player: Player) {
        player.clear("restoration")
        player.clear("restoration_amount")
    }

}

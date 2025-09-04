package content.entity.player.effect

import content.skill.prayer.praying
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class HitpointRestoration {

    @Spawn
    fun spawn(player: Player) {
        if (player.levels.getOffset(Skill.Constitution) < 0) {
            player.softTimers.start("restore_hitpoints")
        }
    }

    @LevelChange(Skill.CONSTITUTION)
    fun level(player: Player, skill: Skill, to: Int) {
        if (to <= 0 || to >= player.levels.getMax(skill) || player.softTimers.contains("restore_hitpoints")) {
            return
        }
        player.softTimers.start("restore_hitpoints")
    }

    @TimerStart("restore_hitpoints")
    fun start(player: Player): Int = TimeUnit.SECONDS.toTicks(6)

    @TimerTick("restore_hitpoints")
    fun tick(player: Player): Int {
        if (player.levels.get(Skill.Constitution) == 0) {
            return TimerState.CANCEL
        }
        val total = player.levels.restore(Skill.Constitution, healAmount(player))
        if (total == 0) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }

    /**
     * Default heal 20 every 12s for 100hp per min.
     */
    fun healAmount(player: Player): Int {
        var heal = 1
        val movement = player["movement", "walk"]
        if (movement == "rest") {
            heal += 1
        } else if (movement == "music") {
            heal += 2
        } else if (player["dream", false]) {
            heal += 4
        }
        if (player.praying("rapid_renewal")) {
            heal += 5
        } else if (player.praying("rapid_heal") || player.equipped(EquipSlot.Cape).id.startsWith("constitution_cape")) {
            heal += 1
        }
        if (player.equipped(EquipSlot.Hands).id == "regen_bracelet") {
            heal *= 2
        }
        return heal
    }
}

package content.entity.player.effect

import content.skill.prayer.praying
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class LevelRestoration {

    val skills = Skill.all.filterNot { it == Skill.Prayer || it == Skill.Summoning || it == Skill.Constitution }

    @Spawn
    fun spawn(player: Player) {
        if (skills.any { player.levels.getOffset(it) != 0 }) {
            player.softTimers.start("restore_stats")
        }
    }

    @LevelChange
    fun level(player: Player, skill: Skill, to: Int) {
        if (skill == Skill.Prayer || skill == Skill.Summoning || skill == Skill.Constitution) {
            return
        }
        if (to == player.levels.getMax(skill) || player.softTimers.contains("restore_stats")) {
            return
        }
        player.softTimers.start("restore_stats")
    }

    @TimerStart("restore_stats")
    fun start(player: Player): Int = TimeUnit.SECONDS.toTicks(60)

    @TimerTick("restore_stats")
    fun tick(player: Player): Int {
        val berserker = player.praying("berserker") && player.hasClock("berserker_cooldown")
        val skip = player.praying("berserker") && !player.hasClock("berserker_cooldown")
        if (skip) {
            val nextInterval = TimeUnit.SECONDS.toTicks(9)
            player.start("berserker_cooldown", nextInterval + 1)
            return nextInterval
        }
        var fullyRestored = true
        for (skill in skills) {
            val offset = player.levels.getOffset(skill)
            if (offset != 0) {
                fullyRestored = false
            }
            if (offset > 0 && !skip) {
                player.levels.drain(skill, 1)
            } else if (offset < 0 && !berserker) {
                val restore = if (player.praying("rapid_restore")) 2 else 1
                player.levels.restore(skill, restore)
            }
        }
        if (fullyRestored) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }
}

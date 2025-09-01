package content.entity.player.stat

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.network.login.protocol.encode.skillLevel
import world.gregs.voidps.type.sub.Experience
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.Spawn

class Experience {

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("xp_counter")
    }

    @Interface("Reset XP Total", "xp_orb", "toplevel*")
    fun reset(player: Player) {
        player["xp_counter"] = 0.0
    }

    @Experience
    fun exp(player: Player, skill: Skill, from: Double, to: Double) {
        val current = player["xp_counter", 0.0]
        val increase = to - from
        player["xp_counter"] = current + increase
        player["lifetime_xp"] = player["lifetime_xp", 0.0] + increase
        val level = player.levels.get(skill)
        player.client?.skillLevel(skill.ordinal, if (skill == Skill.Constitution) level / 10 else level, to.toInt())
    }

    @LevelChange
    fun level(player: Player, skill: Skill, to: Int) {
        if (skill == Skill.Constitution) {
            val exp = player.experience.get(skill)
            player.client?.skillLevel(skill.ordinal, to / 10, exp.toInt())
            player["life_points"] = player.levels.get(Skill.Constitution)
        } else {
            val exp = player.experience.get(skill)
            player.client?.skillLevel(skill.ordinal, to, exp.toInt())
        }
    }
}

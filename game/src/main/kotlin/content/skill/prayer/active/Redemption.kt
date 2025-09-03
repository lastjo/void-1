package content.skill.prayer.active

import content.skill.prayer.praying
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.levelChange
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.LevelChange

class Redemption {

    @LevelChange(Skill.CONSTITUTION)
    fun level(player: Player, skill: Skill, to: Int) {
        if (to <= 0 || to >= player.levels.getMax(skill) / 10 || !player.praying("redemption")) {
            return
        }
        player.levels.set(Skill.Prayer, 0)
        val health = (player.levels.getMax(Skill.Prayer) * 2.5).toInt()
        player.levels.restore(Skill.Constitution, health)
        player.gfx("redemption")
    }

}

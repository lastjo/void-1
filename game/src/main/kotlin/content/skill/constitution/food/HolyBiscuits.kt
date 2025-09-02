package content.skill.constitution.food

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class HolyBiscuits {

    @Consume("holy_biscuits")
    fun eat(player: Player) {
        player.levels.restore(Skill.Prayer, 10)
    }

}

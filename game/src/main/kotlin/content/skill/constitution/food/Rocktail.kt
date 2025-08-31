package content.skill.constitution.food

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Consume

class Rocktail {

    @Consume("rocktail")
    fun eat(player: Player, item: Item): Boolean {
        val range: IntRange = item.def.getOrNull("heals") ?: return false
        val amount = range.random()
        player.levels.boost(Skill.Constitution, amount, maximum = 100)
        return true
    }

}

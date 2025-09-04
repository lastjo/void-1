package content.skill.constitution.food

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class JugOfWine {

    @Consume("jug_of_wine")
    fun drink(player: Player) {
        player.levels.drain(Skill.Attack, 2)
    }
}

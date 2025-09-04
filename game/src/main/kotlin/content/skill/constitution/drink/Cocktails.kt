package content.skill.constitution.drink

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class Cocktails {

    @Consume("wizard_blizzard")
    fun wiz(player: Player) {
        player.levels.boost(Skill.Strength, 6)
        player.levels.drain(Skill.Attack, 4)
    }

    @Consume("short_green_guy")
    fun sgg(player: Player) {
        player.levels.boost(Skill.Strength, 4)
        player.levels.drain(Skill.Attack, 3)
    }

    @Consume("drunk_dragon")
    fun dd(player: Player) {
        player.levels.boost(Skill.Strength, 5)
        player.levels.drain(Skill.Attack, 4)
    }

    @Consume("chocolate_saturday")
    fun choc(player: Player) {
        player.levels.boost(Skill.Strength, 7)
        player.levels.drain(Skill.Attack, 4)
    }

    @Consume("blurberry_special")
    fun spec(player: Player) {
        player.levels.boost(Skill.Strength, 6)
        player.levels.drain(Skill.Attack, 4)
    }
}

package content.skill.constitution.food

import content.entity.player.effect.energy.runEnergy
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class Pies {

    @Consume("admiral_pie*")
    fun admiral(player: Player) {
        player.levels.boost(Skill.Fishing, 5)
    }

    @Consume("fish_pie*")
    fun fish(player: Player) {
        player.levels.boost(Skill.Fishing, 3)
    }

    @Consume("garden_pie*")
    fun garden(player: Player) {
        player.levels.boost(Skill.Farming, 5)
    }

    @Consume("summer_pie*")
    fun summer(player: Player) {
        player.runEnergy += (player.runEnergy / 100) * 10
        player.levels.boost(Skill.Agility, 5)
    }

    @Consume("wild_pie*")
    fun wild(player: Player) {
        player.levels.boost(Skill.Slayer, 4)
        player.levels.boost(Skill.Ranged, 4)
    }

}

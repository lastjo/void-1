package content.skill.constitution.food

import content.entity.player.effect.energy.runEnergy
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class Pies {

    @Consume("admiral_pie*")
    fun admiral(player: Player): Boolean {
        player.levels.boost(Skill.Fishing, 5)
        return false
    }

    @Consume("fish_pie*")
    fun fish(player: Player): Boolean {
        player.levels.boost(Skill.Fishing, 3)
        return false
    }

    @Consume("garden_pie*")
    fun garden(player: Player): Boolean {
        player.levels.boost(Skill.Farming, 5)
        return false
    }

    @Consume("summer_pie*")
    fun summer(player: Player): Boolean {
        player.runEnergy += (player.runEnergy / 100) * 10
        player.levels.boost(Skill.Agility, 5)
        return false
    }

    @Consume("wild_pie*")
    fun wild(player: Player): Boolean {
        player.levels.boost(Skill.Slayer, 4)
        player.levels.boost(Skill.Ranged, 4)
        return false
    }

}

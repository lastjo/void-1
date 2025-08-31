package content.skill.constitution.drink

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class Bottled {

    @Consume("karamjan_rum")
    fun rum(player: Player): Boolean {
        player.levels.boost(Skill.Strength, 5)
        player.levels.drain(Skill.Attack, 4)
        return false
    }

    @Consume("vodka", "gin", "brandy", "whisky")
    fun spirits(player: Player): Boolean {
        player.levels.boost(Skill.Strength, 1, 0.05)
        player.levels.drain(Skill.Attack, 3, 0.02)
        return false
    }

    @Consume("bottle_of_wine")
    fun wine(player: Player): Boolean {
        player.levels.drain(Skill.Attack, 3)
        return false
    }

    @Consume("braindeath_rum")
    fun braindeath(player: Player): Boolean {
        player.levels.boost(Skill.Strength, 3)
        player.levels.boost(Skill.Mining, 1)
        player.levels.drain(Skill.Defence, multiplier = 0.10)
        player.levels.drain(Skill.Attack, multiplier = 0.05)
        player.levels.drain(Skill.Prayer, multiplier = 0.05)
        player.levels.drain(Skill.Ranged, multiplier = 0.05)
        player.levels.drain(Skill.Magic, multiplier = 0.05)
        player.levels.drain(Skill.Agility, multiplier = 0.05)
        player.levels.drain(Skill.Herblore, multiplier = 0.05)
        return false
    }

}

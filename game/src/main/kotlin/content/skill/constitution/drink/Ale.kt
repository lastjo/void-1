package content.skill.constitution.drink

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Consume

class Ale {

    @Consume("bandits_brew")
    fun banditsBrew(player: Player) {
        player.levels.boost(Skill.Thieving, 1)
        player.levels.boost(Skill.Attack, 1)
        player.levels.drain(Skill.Strength, 3, 0.06)
        player.levels.drain(Skill.Defence, 3, 0.06)
    }

    @Consume("beer")
    fun beer(player: Player) {
        player.levels.boost(Skill.Strength, 1, 0.02)
        player.levels.drain(Skill.Attack, 1, 0.06)
        player["dishwater_task"] = true
    }

    @Consume("keg_of_beer*")
    fun keg(player: Player) {
        player.levels.boost(Skill.Strength, 2, 0.10)
        player.levels.drain(Skill.Attack, 5, 0.50)
        player.timers.start("drunk") // TODO screen wobble until teleport
    }

    @Consume("grog")
    fun grog(player: Player) {
        player.levels.boost(Skill.Strength, 3)
        player.levels.drain(Skill.Attack, 6)
    }
}

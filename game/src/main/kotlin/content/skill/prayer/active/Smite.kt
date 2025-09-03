package content.skill.prayer.active

import content.entity.combat.hit.combatAttack
import content.skill.prayer.praying
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Combat

class Smite {

    @Combat
    fun attack(player: Player, target: Character, damage: Int) {
        if (damage <= 40 || !player.praying("smite")) {
            return
        }
        target.levels.drain(Skill.Prayer, damage / 40)
    }

}

package content.skill.magic.book.ancient

import content.entity.combat.hit.characterCombatAttack
import content.skill.magic.spell.Spell
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Combat

class ShadowSpells {

    @Combat(spell = "shadow_*", type = "magic")
    fun combat(source: Character, target: Character, damage: Int, spell: String) {
        if (damage <= 0) {
            return
        }
        Spell.drain(source, target, spell)
    }

}

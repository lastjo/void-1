package content.skill.magic.book.modern

import content.entity.combat.hit.CombatAttack
import content.entity.combat.hit.characterCombatAttack
import content.entity.effect.freeze
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.type.sub.Combat

class BindSpells(private val definitions: SpellDefinitions) {

    @Combat(spell = "bind", type = "magic")
    @Combat(spell = "snare", type = "magic")
    @Combat(spell = "entangle", type = "magic")
    fun attack(source: Character, target: Character, damage: Int, spell: String) {
        if (damage > 0) {
            source.freeze(target, definitions.get(spell)["freeze_ticks"])
        }
    }

}

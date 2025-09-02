package content.skill.magic.book.modern

import content.skill.magic.spell.Spell
import content.skill.magic.spell.spell
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class DrainSpells(private val spellDefinitions: SpellDefinitions) {

    @Combat(type = "magic", stage = CombatStage.PREPARE)
    fun prepare(source: Character, target: Character): Boolean {
        val definition = spellDefinitions.get(source.spell)
        return definition.contains("drain_skill") && !Spell.canDrain(target, definition)
    }

}

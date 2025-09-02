package content.skill.magic.book.ancient

import content.entity.effect.freeze
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.type.sub.Combat

class IceSpells(private val definitions: SpellDefinitions) {

    @Combat(spell = "ice_*", type = "magic")
    fun combat(source: Character, target: Character, damage: Int, spell: String) {
        if (damage <= 0) {
            return
        }
        val ticks: Int = definitions.get(spell)["freeze_ticks"]
        source.freeze(target, ticks)
    }

}

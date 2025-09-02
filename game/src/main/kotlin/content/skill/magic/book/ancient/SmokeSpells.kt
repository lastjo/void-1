package content.skill.magic.book.ancient

import content.entity.effect.toxin.poison
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class SmokeSpells(private val definitions: SpellDefinitions) {

    @Combat(spell = "smoke_*", type = "magic")
    fun combat(source: Character, target: Character, damage: Int, spell: String) {
        if (damage <= 0) {
            return
        }
        if (random.nextDouble() <= 0.2) {
            val poison: Int = definitions.get(spell)["poison_damage"]
            source.poison(target, poison)
        }
    }

}

package content.skill.magic.book.ancient

import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.timer.epochSeconds
import world.gregs.voidps.type.sub.Combat

class MiasmicSpells(private val definitions: SpellDefinitions) {

    @Combat(spell = "miasmic_*", type = "magic")
    fun combat(source: Character, target: Character, damage: Int, spell: String) {
        if (damage <= 0) {
            return
        }
        val seconds: Int = definitions.get(spell)["effect_seconds"]
        target.start("miasmic", seconds, epochSeconds())
    }
}

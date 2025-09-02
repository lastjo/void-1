package content.skill.magic.book.ancient

import content.entity.combat.hit.characterCombatAttack
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Combat

class BloodSpells(private val definitions: SpellDefinitions) {

    @Combat(spell = "blood_*", type = "magic")
    fun combat(source: Character, target: Character, damage: Int, spell: String) {
        if (damage <= 0) {
            return
        }
        val maxHeal: Int = definitions.get(spell)["max_heal"]
        val health = (damage / 4).coerceAtMost(maxHeal)
        source.levels.restore(Skill.Constitution, health)
    }

}

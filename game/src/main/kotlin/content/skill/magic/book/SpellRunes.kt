package content.skill.magic.book

import content.skill.magic.spell.hasSpellItems
import content.skill.magic.spell.spell
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class SpellRunes {

    @Combat(type = "magic", stage = CombatStage.PREPARE)
    fun prepare(player: Player, target: Character): Boolean {
        if (player.spell.isNotBlank() && !player.hasSpellItems(player.spell)) {
            player.clear("autocast")
            return true
        }
        return false
    }
}

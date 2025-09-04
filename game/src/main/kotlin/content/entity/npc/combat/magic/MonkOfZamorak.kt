package content.entity.npc.combat.magic

import content.skill.magic.spell.Spell
import content.skill.magic.spell.spell
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class MonkOfZamorak {

    @Combat(id = "monk_of_zamorak*", stage = CombatStage.PREPARE)
    fun prepare(npc: NPC, target: Player) {
        npc.spell = if (random.nextBoolean() && Spell.canDrain(target, "confuse")) "confuse" else ""
    }
}

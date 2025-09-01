package content.entity.npc.combat.magic

import content.entity.combat.npcCombatPrepare
import content.skill.magic.spell.Spell
import content.skill.magic.spell.spell
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

@Script
class DarkWizards {

    @Combat(id = "dark_wizard_water*")
    fun combat(npc: NPC, target: Player) {
        npc.spell = if (!random.nextBoolean() && Spell.canDrain(target, "confuse")) "confuse" else "water_strike"
    }

    init {
        npcCombatPrepare("dark_wizard_earth*") { npc ->
            npc.spell = if (!random.nextBoolean() && Spell.canDrain(target, "weaken")) "weaken" else "earth_strike"
        }
    }
}

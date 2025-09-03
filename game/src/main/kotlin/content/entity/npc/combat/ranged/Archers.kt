package content.entity.npc.combat.ranged

import content.entity.combat.npcCombatPrepare
import content.skill.ranged.ammo
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Combat

class Archers {

    @Combat(stage = CombatStage.PREPARE)
    fun prepare(npc: NPC, target: Player) {
        npc.ammo = npc.def.getOrNull<String>("ammo") ?: return
    }

}

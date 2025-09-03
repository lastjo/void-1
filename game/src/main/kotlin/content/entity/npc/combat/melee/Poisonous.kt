package content.entity.npc.combat.melee

import content.entity.combat.npcCombatPrepare
import content.entity.effect.toxin.poison
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class Poisonous {

    @Combat(stage = CombatStage.PREPARE)
    fun prepare(npc: NPC, target: Player) {
        val damage = npc.def.getOrNull<Int>("poison") ?: return
        val roll = npc.def["poison_roll", 0]
        if (roll == 0) {
            npc.poison(target, damage)
            return
        }
        if (random.nextInt(roll) == 0) {
            npc.poison(target, damage)
        }
    }

}

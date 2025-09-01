package content.quest.free.the_restless_ghost

import content.entity.combat.killer
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.type.sub.Despawn

class SkeletonWarlock {

    @Despawn("skeleton_warlock")
    fun despawn(npc: NPC) {
        npc.killer?.clear("restless_ghost_warlock")
    }

}

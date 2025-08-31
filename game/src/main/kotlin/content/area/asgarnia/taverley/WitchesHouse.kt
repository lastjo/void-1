package content.area.asgarnia.taverley

import world.gregs.voidps.engine.data.definition.PatrolDefinitions
import world.gregs.voidps.engine.entity.character.mode.Patrol
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.npcSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Spawn

class WitchesHouse(private val patrols: PatrolDefinitions) {

    @Spawn("nora_t_hagg")
    fun spawn(npc: NPC) {
        val patrol = patrols.get("nora_t_hagg")
        npc.mode = Patrol(npc, patrol.waypoints)
    }

}

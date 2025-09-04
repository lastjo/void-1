package content.area.kandarin.ourania

import world.gregs.voidps.engine.data.definition.PatrolDefinitions
import world.gregs.voidps.engine.entity.character.mode.Patrol
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Move
import world.gregs.voidps.type.sub.Spawn

class ZamorakCrafter(
    private val objects: GameObjects,
    private val patrols: PatrolDefinitions,
) {

    @Spawn("zamorak_crafter*")
    fun spawn(npc: NPC) {
        val patrol = patrols.get(if (npc.id == "zamorak_crafter_start") "zamorak_crafter_to_altar" else "zamorak_crafter_to_bank")
        npc.mode = Patrol(npc, patrol.waypoints)
    }

    @Move("zamorak_crafter*", to = [3270, 4856])
    suspend fun start(npc: NPC) {
        npc.delay(5)
        val patrol = patrols.get("zamorak_crafter_to_altar")
        npc.mode = Patrol(npc, patrol.waypoints)
    }

    @Move("zamorak_crafter*", to = [3314, 4811])
    suspend fun repeat(npc: NPC) {
        val altar = objects[Tile(3315, 4810), "ourania_altar"]
        if (altar != null) {
            npc.face(altar)
        }
        npc.delay(4)
        npc.anim("bind_runes")
        npc.gfx("bind_runes")
        npc.delay(4)
        val patrol = patrols.get("zamorak_crafter_to_bank")
        npc.mode = Patrol(npc, patrol.waypoints)
    }
}

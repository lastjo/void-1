package content.area.misthalin.varrock

import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.Hunt

class AshCleaner {

    @Hunt("ash_finder", npc = "ash_cleaner")
    fun hunt(npc: NPC, target: FloorItem) {
        val block: suspend (Boolean) -> Unit = { Publishers.all.npcFloorItemOption(npc, target, "Take", it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasNPCFloorItemOption(npc, target, "Take", it) }
        npc.mode = Interact(npc, target, interact = block, has = check)
    }
}

package content.area.misthalin.varrock

import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.hunt.huntFloorItem
import world.gregs.voidps.engine.entity.item.floor.FloorItemOption
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.Script

@Script
class AshCleaner {

    init {
        huntFloorItem("ash_cleaner", mode = "ash_finder") { npc ->
            Publishers.launch {
                Publishers.all.npcFloorItemOption(npc, target, "Take")
            }
            npc.mode = Interact(npc, target, FloorItemOption(npc, target, "Take"))
        }
    }
}

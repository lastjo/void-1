package content.area.kandarin.barbarian_outpost

import content.entity.obj.door.enterDoor
import content.quest.questCompleted
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.Option

class BarbarianOutpostGate(private val npcs: NPCs) {

    @Option("Open", "barbarian_outpost_gate_left_closed", "barbarian_outpost_gate_right_closed")
    suspend fun operate(player: Player, target: GameObject) {
        if (!player.questCompleted("alfred_grimhands_barcrawl")) {
            val guard = npcs[player.tile.regionLevel].firstOrNull { it.id == "barbarian_guard" } ?: return
            player.talkWith(guard)
            val block: suspend (Boolean) -> Unit = { Publishers.all.playerNPCOption(player, guard, guard.def, "Talk-to", it) }
            val check: (Boolean) -> Boolean = { Publishers.all.hasPlayerNPCOption(player, guard, guard.def, "Talk-to", it) }
            player.mode = Interact(player, guard, interact = block, has = check)
            return
        }
        player.walkToDelay(player.tile.copy(y = player.tile.y.coerceIn(2569, 3570)))
        player.enterDoor(target, delay = 2)
    }
}

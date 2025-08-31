package world.gregs.voidps.engine.client.ui.dialogue

import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.TargetContext
import world.gregs.voidps.engine.suspend.SuspendableContext
import world.gregs.voidps.engine.suspend.Suspension

class Dialogue(
    override val character: Player,
    override val target: NPC,
) : TargetContext<Player, NPC>, SuspendableContext<Player> {
    override suspend fun pause(ticks: Int) {
        Suspension.start(character, ticks)
    }
}

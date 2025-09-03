package content.area.asgarnia.falador

import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.npc
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class Iconis {

    @Option("Talk-to", "iconis")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        if (!World.members) {
            nonMember()
            return@talkWith
        }
    }
    @Option("Take-picture", "iconis")
    suspend fun take(player: Player, npc: NPC) = player.talkWith(npc) {
        if (!World.members) {
            nonMember()
            return@talkWith
        }
    }

    suspend fun Dialogue.nonMember() {
        npc<Talk>("Good day! I'm afraid you can't use the booth's services on a non-members world. Film costs a lot you know!")
    }
}

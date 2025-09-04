package content.area.misthalin.varrock.palace

import content.entity.player.dialogue.Frustrated
import content.entity.player.dialogue.Surprised
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Option

class SurokMagis {

    @Option("Talk-to", "surok_magis_*")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Frustrated>("Can't you see I'm very busy here? Be off with you!")
        player<Surprised>("Oh. Sorry.")
    }
}

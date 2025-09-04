package content.area.kharidian_desert.al_kharid

import content.entity.npc.shop.openShop
import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Option

class Ranael {

    @Option("Talk-to", "ranael")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Neutral>("Do you want to buy any armoured skirts? Designed especially for ladies who like to fight.")
        choice {
            option<Neutral>("Yes please.") {
                player.openShop("ranaels_super_skirt_store")
            }
            option<Neutral>("No thank you, that's not my scene.")
        }
    }
}

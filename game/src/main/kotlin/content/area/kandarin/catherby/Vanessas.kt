package content.area.kandarin.catherby

import content.entity.npc.shop.openShop
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class Vanessas {

    @Option("Talk-to", "vanessa")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Talk>("Hello. How can I help you?")
        choice {
            option("What are you selling?") {
                player.openShop("vanessas_farming_shop")
            }

            option("Can you give me any Farming advice?") {
                player<Talk>("Can you give me any Farming advice?")
                npc<Talk>("Yes - ask a gardener.")
            }

            option("I'm okay, thank you.") {
                player<Talk>("I'm okay, thank you.")
            }
        }
    }

}

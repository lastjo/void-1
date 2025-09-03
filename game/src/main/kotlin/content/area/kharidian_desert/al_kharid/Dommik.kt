package content.area.kharidian_desert.al_kharid

import content.entity.npc.shop.openShop
import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class Dommik {

    @Option("Talk-to", "dommik")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Happy>("Would you like to buy some crafting equipment?")
        choice {
            option<Neutral>("No thanks; I've got all the Crafting equipment I need.") {
                npc<Happy>("Okay. Fare well on your travels.")
            }
            option<Neutral>("Let's see what you've got, then.") {
                player.openShop("dommiks_crafting_store")
            }
        }
    }

    @Option("Trade", "dommik")
    fun trade(player: Player, target: NPC) {
        player.openShop("dommiks_crafting_store")
    }

}

package content.area.kandarin.catherby

import content.entity.npc.shop.openShop
import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.*
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Option

class CandleMaker {

    // TODO: add Merlin's Crystal quest as that how you got black candle from the shop

    @Option("Talk-to", "candle_maker")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Talk>("Hi! Would you be interested in some of my fine candles?")
        choice {
            option("Yes please.") {
                player<Talk>("Yes please.")
                player.openShop("candle_shop")
            }

            option("No thank you.") {
                player<Talk>("No thank you.")
                // Ends dialogue naturally
            }
        }
    }

    @Option("Trade", "candle_maker")
    fun trade(player: Player, target: NPC) {
        player.openShop("candle_shop")
    }
}

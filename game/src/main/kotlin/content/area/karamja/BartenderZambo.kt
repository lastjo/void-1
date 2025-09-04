package content.area.karamja

import content.entity.npc.shop.openShop
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.quest.miniquest.alfred_grimhands_barcrawl.barCrawlDrink
import content.quest.miniquest.alfred_grimhands_barcrawl.barCrawlFilter
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.noInterest
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class BartenderZambo {

    @Option("Talk-to", "bartender_zambo")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Talk>("Hey, are you wanting to try some of my fine wines and spirits? All brewed locally on Karamja.")
        choice {
            option("Yes, please.") {
                player.openShop("karamja_wines_spirits_and_beers")
            }
            option<Talk>("No, thank you.")
            option("I'm doing Alfred Grimhand's barcrawl.", filter = { barCrawlFilter(player, target) }) {
                barCrawl()
            }
        }
    }

    @UseOn("barcrawl_card", "bartender_zambo")
    suspend fun use(player: Player, npc: NPC) {
        if (player.containsVarbit("barcrawl_signatures", "ape_bite_liqueur")) {
            player.noInterest() // TODO proper message
            return
        }
        player.talkWith(npc) {
            barCrawl()
        }
    }

    suspend fun Dialogue.barCrawl() = barCrawlDrink(
        effects = {
            player.say("Mmmmm, dat was luverly...")
        },
    )
}

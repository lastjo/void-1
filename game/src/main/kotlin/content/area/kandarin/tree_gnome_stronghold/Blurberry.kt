package content.area.kandarin.tree_gnome_stronghold

import content.entity.combat.hit.damage
import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.miniquest.alfred_grimhands_barcrawl.barCrawlDrink
import content.quest.miniquest.alfred_grimhands_barcrawl.barCrawlFilter
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.noInterest
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class Blurberry {

    @Option("Talk-to", "blurberry")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        player<Talk>("Hello.")
        npc<Neutral>("Well hello there traveller. If you're looking for a cocktail the barman will happily make you one.")
        npc<Neutral>("Or if you're looking for some training in cocktail making, then I'm your gnome! Aluft Gianne jnr. is looking for gnome cooks and bartenders to help in his new venture, so it's a useful skill to have.")
        choice {
            option<Talk>("No thanks, I prefer to stay this side of the bar.")
            option("I'm doing Alfred Grimhand's barcrawl.", filter = barCrawlFilter) {
                barCrawl()
            }
        }
    }

    @UseOn("barcrawl_card", "blurberry")
    suspend fun use(player: Player, npc: NPC) {
        if (player.containsVarbit("barcrawl_signatures", "fire_toad_blast")) {
            player.noInterest() // TODO proper message
            return
        }
        player.talkWith(npc) {
            barCrawl()
        }
    }

    suspend fun Dialogue.barCrawl() = barCrawlDrink(
        start = { npc<Happy>("Ah, you've come to the best stop on your list! I'll give you my famous Fire Toad Blast! It'll cost you 10 coins.") },
        effects = { player.damage(10) },
    )
}

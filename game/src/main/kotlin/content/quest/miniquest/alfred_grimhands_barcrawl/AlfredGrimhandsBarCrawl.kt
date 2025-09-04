package content.quest.miniquest.alfred_grimhands_barcrawl

import content.entity.player.dialogue.Sad
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.messageScroll
import content.quest.quest
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.Colours
import world.gregs.voidps.engine.client.ui.chat.toTag
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.TargetContext
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Inventory

suspend fun Dialogue.barCrawlDrink(
    start: (suspend Dialogue.() -> Unit)? = null,
    effects: suspend Dialogue.() -> Unit = {},
) {
    player<Talk>("I'm doing Alfred Grimhand's Barcrawl.")
    val info: Map<String, Any> = target.def.getOrNull("bar_crawl") ?: return
    start?.invoke(this) ?: npc<Talk>(info["start"] as String)
    val id = info["id"] as String
    if (!player.inventory.remove("coins", info["price"] as Int)) {
        player<Sad>(info["insufficient"] as String)
        return
    }
    player.message(info["give"] as String)
    player.delay(4)
    player.message(info["drink"] as String)
    player.delay(4)
    player.message(info["effect"] as String)
    player.delay(4)
    (info["sign"] as? String)?.let { player.message(it) }
    player.addVarbit("barcrawl_signatures", id)
    effects()
}

val barCrawlFilter: TargetContext<Player, NPC>.() -> Boolean = filter@{
    val info: Map<String, Any> = target.def.getOrNull("bar_crawl") ?: return@filter false
    val id = info["id"] as String
    player.quest("alfred_grimhands_barcrawl") == "signatures" && !player.containsVarbit("barcrawl_signatures", id)
}

class AlfredGrimhandsBarCrawl {

    @Inventory("Read", "barcrawl_card")
    fun readCard(player: Player) {
        val signatures: List<String> = player["barcrawl_signatures", emptyList()]
        if (signatures.size == 10) {
            player.message("You are too drunk to be able to read the barcrawl card.")
            return
        }
        player.messageScroll(
            listOf(
                "${Colours.BLUE.toTag()}The Official Alfred Grimhand Barcrawl!",
                "",
                player.line("Blue Moon Inn", "uncle_humphreys_gutrot"),
                player.line("Blurberry's Bar", "fire_toad_blast"),
                player.line("Dead Man's Chest", "supergrog"),
                player.line("Dragon Inn", "fire_brandy"),
                player.line("Flying Horse Inn", "heart_stopper"),
                player.line("Forester's Arms", "liverbane_ale"),
                player.line("Jolly Boar Inn", "olde_suspiciouse"),
                player.line("Karamja Spirits Bar", "ape_bite_liqueur"),
                player.line("Rising Sun Inn", "hand_of_death_cocktail"),
                player.line("Rusty Anchor Inn", "black_skull_ale"),
            ),
        )
    }

    fun Player.line(name: String, id: String): String {
        val complete = containsVarbit("barcrawl_signatures", id)
        return "<${Colours.bool(complete)}>$name - ${if (complete) "Completed!" else "Not Completed"}"
    }
}

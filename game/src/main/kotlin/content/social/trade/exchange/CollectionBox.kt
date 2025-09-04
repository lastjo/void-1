package content.social.trade.exchange

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.type.sub.Open

class CollectionBox(private val exchange: GrandExchange) {

    @Open("collection_box")
    fun open(player: Player, id: String) {
        for (slot in 0 until 6) {
            exchange.refresh(player, slot)
            player.interfaceOptions.unlockAll(id, "collection_box_$slot", 0..4)
            player.sendInventory("collection_box_$slot")
        }
    }
}

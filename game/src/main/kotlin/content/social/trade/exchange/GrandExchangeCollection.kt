package content.social.trade.exchange

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.bank.noted
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.exchange.ExchangeHistory
import world.gregs.voidps.engine.data.exchange.ExchangeOffer
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.type.sub.Interface

class GrandExchangeCollection(private val exchange: GrandExchange) {

    val logger = InlineLogger()

    @Interface("Collect*", "collect_slot_*", "grand_exchange")
    fun collectSlot(player: Player, component: String, option: String) {
        val index = component.removePrefix("collect_slot_").toInt()
        val box: Int = player["grand_exchange_box"] ?: return
        collect(player, option, box, index)
    }

    @Interface("Collect*", "collection_box_*", "grand_exchange")
    fun collectBox(player: Player, component: String, option: String, itemSlot: Int) {
        val box = component.removePrefix("collection_box_").toInt()
        val index = if (itemSlot == 2) 1 else 0
        collect(player, option, box, index)
    }

    @Interface("Abort Offer", "offer_abort", "grand_exchange")
    fun abortOffer(player: Player) {
        val slot: Int = player["grand_exchange_box"] ?: return
        abort(player, slot)
    }

    @Interface("Abort Offer", "view_offer_*", "grand_exchange")
    fun abortView(player: Player, component: String) {
        val slot = component.removePrefix("view_offer_").toInt()
        if (slot > 1 && !World.members) {
            return
        }
        abort(player, slot)
    }

    fun collect(player: Player, option: String, box: Int, index: Int) {
        val offer = player.offers.getOrNull(box) ?: return
        val collectionBox = player.inventories.inventory("collection_box_$box")
        val item = collectionBox[index]
        var noted = item
        // Option 1 is to collect noted if amount > 1 otherwise options flip
        if ((item.amount > 1 && option == "Collect_notes") || (item.amount == 1 && option == "Collect")) {
            noted = item.noted ?: item
        }
        player.inventory.transaction {
            val txn = link(collectionBox)
            val added = addToLimit(noted.id, item.amount)
            if (added < 1) {
                error = TransactionError.Full()
            }
            txn.remove(item.id, added)
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> if (collectionBox.isEmpty()) {
                if (offer.state.cancelled) {
                    if (offer.completed > 0) {
                        player.history.add(0, ExchangeHistory(offer))
                    }
                    player.offers[box] = ExchangeOffer.EMPTY
                    exchange.offers.remove(offer)
                    GrandExchange.clearSelection(player)
                }
                exchange.refresh(player, box)
            } else if (collectionBox.contains(item.id)) {
                player.inventoryFull()
            }
            else -> logger.warn { "Issue collecting items from grand exchange ${player.inventory.transaction.error} ${player.name} $item $index" }
        }
    }

    fun abort(player: Player, slot: Int) {
        exchange.cancel(player, slot)
        // https://youtu.be/3ussM7P1j00?si=IHR8ZXl2kN0bjIfx&t=398
        player.message("Abort request acknowledged. Please be aware that your offer may have already been completed.")
    }
}

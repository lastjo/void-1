package content.social.trade

import content.entity.player.dialogue.type.intEntry
import content.social.trade.Trade.isTrading
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.inv.transact.operation.ClearItem.clear
import world.gregs.voidps.engine.inv.transact.operation.RemoveItemLimit.removeToLimit
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface

/**
 * Removing an item from an offer or loan
 */
class TradeRemove {

    @Interface(component = "offer_options", id = "trade_main")
    suspend fun remove(player: Player, item: Item, itemSlot: Int, option: String) {
        val amount = when (option) {
            "Remove" -> 1
            "Remove-5" -> 5
            "Remove-10" -> 10
            "Remove-All" -> player.offer.count(item.id)
            "Remove-X" -> player.intEntry("Enter amount:")
            else -> return
        }
        remove(player, item.id, itemSlot, amount)
    }

    @Interface("Value", "offer_options", "trade_main")
    fun value(player: Player, item: Item) {
        player.message("${item.def.name} is priceless!", ChatType.Trade)
    }

    fun remove(player: Player, id: String, slot: Int, amount: Int) {
        if (!isTrading(player, amount)) {
            return
        }
        player.offer.transaction {
            val added = link(player.inventory).addToLimit(id, amount)
            if (!inventory.stackable(id) && added == 1) {
                clear(slot)
            } else {
                removeToLimit(id, added)
            }
        }
    }

    @Interface("Remove", "loan_item", "trade_main")
    fun removeLend(player: Player, item: Item) {
        if (!isTrading(player, 1)) {
            return
        }
        player.loan.transaction {
            clear(0)
            link(player.inventory).add(item.id, 1)
        }
    }
}

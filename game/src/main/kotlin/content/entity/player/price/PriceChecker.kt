package content.entity.player.price

import content.entity.player.bank.isNote
import content.entity.player.bank.noted
import content.entity.player.dialogue.type.intEntry
import content.entity.player.modal.Tab
import content.entity.player.modal.tab
import content.social.trade.offer
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.dialogue
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.moveAll
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.MoveItemLimit.moveToLimit
import world.gregs.voidps.type.sub.*

class PriceChecker {

    /*
        Price checker interface
     */
    @Open("price_checker")
    fun open(player: Player, id: String) {
        player.interfaceOptions.unlockAll(id, "items", 0 until 28)
        player["price_checker_total"] = 0
        player["price_checker_limit"] = Int.MAX_VALUE
        player.open("price_checker_side")
    }

    @Interface("Remove-*", "items", "price_checker")
    suspend fun click(player: Player, option: String, item: Item) = player.dialogue {
        val amount = when (option) {
            "Remove-1" -> 1
            "Remove-5" -> 5
            "Remove-10" -> 10
            "Remove-All" -> player.offer.count(item.id)
            "Remove-X" -> intEntry("Enter amount:")
            else -> return@dialogue
        }
        player.offer.transaction {
            moveToLimit(item.id, amount, player.inventory)
        }
        when (player.offer.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            else -> {}
        }
    }

    @Close("price_checker")
    fun close(player: Player) {
        player.close("price_checker_side")
        player.sendScript("clear_dialogues")
        player.offer.moveAll(player.inventory)
    }

    /*
        Price checker inventory interface
     */

    @Open("price_checker_side")
    fun openSide(player: Player, id: String) {
        player.tab(Tab.Inventory)
        player.interfaceOptions.send(id, "items")
        player.interfaceOptions.unlockAll(id, "items", 0 until 28)
        player.sendInventory(player.inventory)
    }

    @Interface("Add*", "items", "price_checker_side")
    suspend fun add(player: Player, option: String, item: Item) = player.dialogue {
        val amount = when (option) {
            "Add" -> 1
            "Add-5" -> 5
            "Add-10" -> 10
            "Add-All" -> player.inventory.count(item.id)
            "Add-X" -> intEntry("Enter amount:")
            else -> return@dialogue
        }
        player.inventory.transaction {
            moveToLimit(item.id, amount, player.offer)
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Invalid -> player.message("That item is not tradeable.")
            else -> {}
        }
    }

    @Close("price_checker_side")
    fun closeSide(player: Player) {
        player.open("inventory")
    }

    @InventoryUpdated("trade_offer")
    fun update(player: Player) {
        var total = 0L
        for (index in player.offer.indices) {
            val item = player.offer[index]
            if (item.isEmpty()) {
                continue
            }
            val notNoted = if (item.isNote) item.noted ?: item else item
            val price = notNoted.def["price", notNoted.def.cost]
            player["value_$index"] = price
            total += price * item.amount
        }
        player["price_checker_total"] = total.toInt()
    }
}

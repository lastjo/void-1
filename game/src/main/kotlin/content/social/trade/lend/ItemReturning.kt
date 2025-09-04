package content.social.trade.lend

import com.github.michaelbull.logging.InlineLogger
import content.social.trade.lend.Loan.getExpiry
import content.social.trade.lend.Loan.returnLoan
import content.social.trade.returnedItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.MoveItem.moveAll
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class ItemReturning(private val players: Players) {

    val logger = InlineLogger()

    @Open("returned_items")
    fun open(player: Player) {
        player.sendInventory(player.returnedItems)
    }

    @Interface("Reclaim", "item", "returned_items")
    fun reclaim(player: Player) {
        if (!player.contains("lent_item_id")) {
            returnItem(player)
            return
        }
        if (player.contains("lend_timeout")) {
            player.message("Your item will be returned to you ${getExpiry(player, "lend_timeout")}.") // TODO real message
            return
        }
        if (!player.contains("lent_to")) {
            logger.warn { "Invalid item lending state; can't force claim an item when target has already logged out." }
            return
        }

        player.message("Demanding return of item.")
        val name: String? = player["lent_to"]
        val borrower = if (name == null) null else players.get(name)
        if (borrower == null) {
            player.message("There was an issue returning your item.")
            logger.warn { "Unable to find lent item borrower '$name'." }
            return
        }

        player.softTimers.clear("loan_message")
        player.clear("lent_item_id")
        player.clear("lent_item_amount")
        returnLoan(borrower)
        returnItem(player)
        player.message("Your item has been returned.")
    }

    fun returnItem(player: Player) {
        player.returnedItems.transaction {
            moveAll(player.inventory)
        }
        when (player.returnedItems.transaction.error) {
            TransactionError.Invalid -> logger.info { "Return item issue: $player" }
            is TransactionError.Full -> player.inventoryFull()
            else -> player.clear("lent_to")
        }
    }
}

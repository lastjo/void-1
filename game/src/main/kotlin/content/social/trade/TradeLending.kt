package content.social.trade

import content.entity.player.dialogue.type.intEntry
import content.social.trade.Trade.getPartner
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.restrict.ItemRestrictionRule
import world.gregs.voidps.engine.inv.transact.operation.SwapItem.swap
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Spawn

/**
 * Offering an item to lend for a duration
 */
class TradeLending(private val definitions: ItemDefinitions) {

    private val lendRestriction = object : ItemRestrictionRule {
        override fun restricted(id: String) = definitions.get(id).lendId == -1
    }

    @Spawn
    fun spawn(player: Player) {
        player.loan.itemRule = lendRestriction
    }

    @Interface("Specify", "loan_time", "trade_main")
    suspend fun specify(player: Player) {
        val hours = player.intEntry("Set the loan duration in hours: (1 - 72)<br>(Enter <col=7f0000>0</col> for 'Just until logout'.)").coerceIn(0, 72)
        setLend(player, hours)
    }

    @Interface("‘Until Logout‘", "loan_time", "trade_main")
    fun untilLogout(player: Player) {
        setLend(player, 0)
    }

    @Interface("Lend", "offer", "trade_side")
    fun lend(player: Player, item: Item, itemSlot: Int) {
        val partner = getPartner(player) ?: return
        lend(player, partner, item.id, itemSlot)
    }

    // Item must have a lent version

    fun setLend(player: Player, time: Int) {
        player["lend_time"] = time
        val partner = getPartner(player) ?: return
        partner["other_lend_time"] = time
    }

    fun lend(player: Player, other: Player, id: String, slot: Int) {
        if (!Trade.isTrading(player, 1)) {
            return
        }
        if (player.returnedItems.isFull()) {
            player.message("You are already lending an item, you can't lend another.")
            return
        }

        if (other.contains("borrowed_item")) {
            player.message("They are already borrowing an item and can't borrow another.")
            return
        }

        if (player.loan.restricted(id)) {
            player.message("That item cannot be lent.")
            return
        }

        val lent = player.inventory.transaction {
            swap(slot, player.loan, 0)
        }
        if (!lent) {
            player.message("That item cannot be lent.")
            return
        }
        player["lend_time"] = 0
        other["other_lend_time"] = 0
    }
}

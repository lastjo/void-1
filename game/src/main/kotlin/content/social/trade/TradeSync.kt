package content.social.trade

import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.req.hasRequest
import world.gregs.voidps.engine.entity.character.player.req.removeRequest
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.InventorySlotChanged
import world.gregs.voidps.type.sub.InventoryUpdated

/**
 * Persist updates on an offer to the other player
 */
class TradeSync(
    private val interfaceDefinitions: InterfaceDefinitions,
    private val inventoryDefinitions: InventoryDefinitions,
) {

    @InventorySlotChanged("trade_offer")
    fun change(player: Player, itemSlot: Int, item: Item, from: String, fromSlot: Int, fromItem: Item) {
        val other: Player = Trade.getPartner(player) ?: return
        applyUpdates(other.otherOffer, itemSlot, item, from, fromSlot)
        val warn = player.hasRequest(other, "accept_trade") && removedAnyItems(item, fromItem)
        if (warn) {
            highlightRemovedSlots(player, other, item, fromItem, itemSlot)
        }
        modified(player, other, warn)
        updateValue(player, other)
    }

    @InventorySlotChanged("item_loan")
    fun loanChange(player: Player, itemSlot: Int, item: Item, from: String, fromSlot: Int, fromItem: Item) {
        val other: Player = Trade.getPartner(player) ?: return
        applyUpdates(other.otherLoan, itemSlot, item, from, fromSlot)
        val warn = player.hasRequest(other, "accept_trade") && removedAnyItems(item, fromItem)
        modified(player, other, warn)
    }

    @InventoryUpdated("inventory")
    fun update(player: Player) {
        val other: Player = Trade.getPartner(player) ?: return
        updateInventorySpaces(other, player)
    }

    /*
        Offer
     */

    fun highlightRemovedSlots(player: Player, other: Player, item: Item, fromItem: Item, index: Int) {
        if (item.amount < fromItem.amount) {
            player.warn("trade_main", "offer_warning", index)
            other.warn("trade_main", "other_warning", index)
        }
    }

    fun Player.warn(id: String, componentId: String, slot: Int) {
        val component = interfaceDefinitions.getComponent(id, componentId) ?: return
        val inventory = inventoryDefinitions.get(component["inventory", ""])
        sendScript("trade_warning", component.id, inventory["width", 0.0], inventory["height", 0.0], slot)
    }

    fun updateValue(player: Player, other: Player) {
        val value = player.offer.calculateValue().toInt()
        player["offer_value"] = value
        other["other_offer_value"] = value
    }

    /*
        Loan
     */

    fun applyUpdates(inventory: Inventory, index: Int, item: Item, from: String, fromIndex: Int) {
        inventory.transaction {
            set(index, item, from, fromIndex)
        }
    }

    fun removedAnyItems(item: Item, fromItem: Item) = item.amount < fromItem.amount

    fun modified(player: Player, other: Player, warned: Boolean) {
        if (warned) {
            player["offer_modified"] = true
            other["other_offer_modified"] = true
        }
        player.removeRequest(other, "accept_trade")
        player.interfaces.sendText("trade_main", "status", "")
        other.interfaces.sendText("trade_main", "status", "")
    }

    /*
        Item count
     */

    fun updateInventorySpaces(player: Player, other: Player) {
        player.interfaces.sendText("trade_main", "slots", "has ${other.inventory.spaces} free inventory slots.")
    }
}

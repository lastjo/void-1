package world.gregs.voidps.engine.inv.transact

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.*
import java.util.*

/**
 * Tracks the changes made to the inventory and allows for sending these changes to the appropriate recipients.
 */
class ChangeManager(
    private val inventory: Inventory,
) {
    private val events = mutableSetOf<Player>()

    val changes: Stack<Change> = Stack()

    data class Change(val from: String, val index: Int, val previous: Item, val fromIndex: Int, val item: Item)

    /**
     * Track a change of an item in the inventory.
     * @param from the inventory id the item is from
     * @param index the index of the item in the inventory
     * @param previous the previous state of the item
     * @param fromIndex the index in the inventory the item was from
     * @param item the current state of the item
     */
    fun track(from: String, index: Int, previous: Item, fromIndex: Int, item: Item) {
        changes.add(Change(from, index, previous, fromIndex, item))
    }

    /**
     * Adds [player] to the list of recipients of [InventorySlotChanged] updates in this inventory.
     */
    fun bind(player: Player) {
        this.events.add(player)
    }

    /**
     * Removes [events] to the list of recipients of [InventorySlotChanged] updates in this inventory.
     */
    fun unbind(events: Player) {
        this.events.remove(events)
    }

    /**
     * Send the tracked changes to the appropriate recipients.
     */
    fun send() {
        if (changes.isEmpty()) {
            return
        }
        for (player in events) {
            Publishers.all.inventoryUpdated(player, inventory.id)
            for (change in changes) {
                if (change.previous.isNotEmpty()) {
                    Publishers.all.itemRemoved(player, change.previous, change.index, inventory.id)
                }
                if (change.item.isNotEmpty()) {
                    Publishers.all.itemAdded(player, change.item, change.index, inventory.id)
                }
                Publishers.all.inventoryChanged(player, inventory.id, change.index, change.item, change.from, change.fromIndex, change.previous)
            }
        }
    }

    /**
     * Clear the tracked changes.
     */
    fun clear() {
        changes.clear()
    }
}

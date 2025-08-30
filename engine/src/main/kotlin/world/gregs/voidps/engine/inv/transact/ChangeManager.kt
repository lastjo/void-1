package world.gregs.voidps.engine.inv.transact

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Event
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.*
import java.util.*

/**
 * Tracks the changes made to the inventory and allows for sending these changes to the appropriate recipients.
 */
class ChangeManager(
    private val inventory: Inventory,
) {
    private val changes: Stack<Event> = Stack()
    private val events = mutableSetOf<Player>()
    private lateinit var publishers: Publishers

    val itemChanges: Stack<Change> = Stack()

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
        itemChanges.add(Change(from, index, previous, fromIndex, item))
    }

    /**
     * Adds [player] to the list of recipients of [InventorySlotChanged] updates in this inventory.
     */
    fun bind(player: Player, publishers: Publishers) {
        this.publishers = publishers
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
        if (itemChanges.isEmpty()) {
            return
        }
        val changes: Stack<Event> = Stack()
        for (player in events) {
            publishers.publishPlayer(player, "inventory_update", inventory.id)
            for (change in itemChanges) {
                if (change.previous.isNotEmpty()) {
                    publishers.itemRemoved(player, change.previous, change.index, inventory.id)
                    changes.add(ItemRemoved(inventory.id, change.index, change.previous))
                }
                if (change.item.isNotEmpty()) {
                    publishers.itemAdded(player, change.item, change.index, inventory.id)
                    changes.add(ItemAdded(inventory.id, change.index, change.item))
                }
                publishers.inventoryChanged(player, change.previous, change.index, inventory.id)
                changes.add(InventorySlotChanged(inventory.id, change.index, change.item, change.from, change.fromIndex, change.previous))
            }
        }
        if (changes.isEmpty()) {
            return
        }
        val update = InventoryUpdate(inventory.id, changes.filterIsInstance<InventorySlotChanged>())
        for (events in events) {
            events.emit(update)
            for (change in changes) {
                events.emit(change)
            }
        }
    }

    /**
     * Clear the tracked changes.
     */
    fun clear() {
        itemChanges.clear()
        changes.clear()
    }
}

package world.gregs.voidps.engine.inv

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Events
import world.gregs.voidps.engine.event.Publishers
import kotlin.test.assertEquals

class InventorySlotChangedTest {

    private lateinit var inventory: Inventory

    private var additions = 0
    private var removals = 0
    private var changes = 0

    @BeforeEach
    fun setup() {
        additions = 0
        removals = 0
        changes = 0
        Publishers.set(object : Publishers {
            override fun itemAdded(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                additions++
                return super.itemAdded(player, item, itemSlot, inventory)
            }

            override fun itemRemoved(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                removals++
                return super.itemRemoved(player, item, itemSlot, inventory)
            }

            override fun inventoryUpdated(player: Player, inventory: String): Boolean {
                changes++
                return super.inventoryUpdated(player, inventory)
            }
        })
        inventory = Inventory.debug(1, id = "inventory")
        val dispatcher = Player()
        inventory.transaction.changes.bind(dispatcher)
        Events.events.clear()
    }

    @Test
    fun `Track changes`() {
        val manager = inventory.transaction.changes
        manager.track("inventory", 1, Item.EMPTY, 1, Item("item", 1))
        manager.send()
        manager.clear()

        assertEquals(1, changes)
    }

    @Test
    fun `Track additions`() {
        val manager = inventory.transaction.changes
        manager.track("", 1, Item.EMPTY, 0, Item("coins", 1))
        manager.send()
        manager.clear()

        assertEquals(1, additions)
    }

    @Test
    fun `Track removals`() {
        val manager = inventory.transaction.changes
        manager.track("bank", 1, Item("coins", 1), 0, Item.EMPTY)
        manager.send()
        manager.clear()

        assertEquals(1, removals)
    }

    @Test
    fun `Replacing identical items counts as both additions and removals`() {
        val manager = inventory.transaction.changes
        manager.track("inventory", 1, Item("coins", 1), 0, Item("coins", 1))
        manager.send()
        manager.clear()

        assertEquals(1, additions)
        assertEquals(1, removals)
    }
}

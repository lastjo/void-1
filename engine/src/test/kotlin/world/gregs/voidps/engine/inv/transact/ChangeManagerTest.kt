package world.gregs.voidps.engine.inv.transact

import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.Inventory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ChangeManagerTest {

    private lateinit var change: ChangeManager
    private lateinit var inventory: Inventory

    @BeforeEach
    fun setup() {
        inventory = Inventory.debug(1)
        change = ChangeManager(inventory)
    }

    @AfterEach
    fun cleanup() {
        Publishers.clear()
    }

    @Test
    fun `Track and send changes`() {
        val player = mockk<Player>(relaxed = true)
        var changes = 0
        var updates = 0
        Publishers.set(object : Publishers {
            override fun inventoryUpdated(player: Player, inventory: String): Boolean {
                updates++
                return super.inventoryUpdated(player, inventory)
            }

            override fun inventoryChanged(player: Player, inventory: String, itemSlot: Int, item: Item, from: String, fromSlot: Int, fromItem: Item): Boolean {
                changes++
                return super.inventoryChanged(player, inventory, itemSlot, item, from, fromSlot, fromItem)
            }
        })
        change.bind(player)
        change.track(from = "inventory", index = 1, previous = Item.EMPTY, fromIndex = 1, item = Item("item", 1))
        change.send()
        assertEquals(1, changes)
        assertEquals(1, updates)
    }

    @Test
    fun `Clear tracked changes`() {
        val events = mockk<Player>(relaxed = true)
        Publishers.set(object : Publishers {
            override fun itemAdded(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                throw IllegalStateException()
            }

            override fun itemRemoved(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                throw IllegalStateException()
            }

            override fun publishPlayer(player: Player, event: String, id: Any): Boolean {
                throw IllegalStateException()
            }

            override fun inventoryChanged(player: Player, inventory: String, index: Int, item: Item, from: String, fromSlot: Int, fromItem: Item): Boolean {
                throw IllegalStateException()
            }
        })
        change.bind(events)
        change.track("inventory", 1, Item.EMPTY, 1, Item("item", 1))
        change.clear()
        change.send()
        assertTrue(change.changes.isEmpty())

    }
}

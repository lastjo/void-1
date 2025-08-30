package world.gregs.voidps.engine.inv.transact

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.InventorySlotChanged
import world.gregs.voidps.engine.inv.InventoryUpdate
import kotlin.test.assertTrue

internal class ChangeManagerTest {

    private lateinit var change: ChangeManager
    private lateinit var inventory: Inventory

    @BeforeEach
    fun setup() {
        inventory = Inventory.debug(1)
        change = ChangeManager(inventory)
    }

    @Test
    fun `Track and send changes`() {
        val events = mockk<Player>(relaxed = true)
        change.bind(events, object : Publishers() {})
        change.track(from = "inventory", index = 1, previous = Item.EMPTY, fromIndex = 1, item = Item("item", 1))
        change.send()
        verify {
            events.emit(any<InventorySlotChanged>())
            events.emit(any<InventoryUpdate>())
        }
    }

    @Test
    fun `Clear tracked changes`() {
        val events = mockk<Player>(relaxed = true)
        change.bind(events, object : Publishers() {
            override fun itemAdded(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                throw IllegalStateException()
            }

            override fun itemRemoved(player: Player, item: Item, itemSlot: Int, inventory: String): Boolean {
                throw IllegalStateException()
            }

            override fun publishPlayer(player: Player, event: String, id: String): Boolean {
                throw IllegalStateException()
            }

            override fun inventoryChanged(player: Player, previous: Item, index: Int, id: String): Boolean {
                throw IllegalStateException()
            }
        })
        change.track("inventory", 1, Item.EMPTY, 1, Item("item", 1))
        change.clear()
        change.send()
        assertTrue(change.itemChanges.isEmpty())

    }
}

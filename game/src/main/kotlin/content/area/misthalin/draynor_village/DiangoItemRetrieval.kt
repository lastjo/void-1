package content.area.misthalin.draynor_village

import content.entity.player.bank.ownsItem
import world.gregs.voidps.cache.definition.data.InterfaceDefinition
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.ClearItem.clear
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class DiangoItemRetrieval(
    private val inventoryDefinitions: InventoryDefinitions,
    private val itemDefinitions: ItemDefinitions,
) {

    val itemLimit = 48
    val container = InterfaceDefinition.pack(468, 2)
    val scrollbar = InterfaceDefinition.pack(468, 3)

    @Interface("Claim", "items", "diangos_item_retrieval")
    fun claim(player: Player, item: Item) {
        when (item.id) {
            "more" -> {
                player["retrieve_more"] = true
                player.sendScript("scrollbar_resize", scrollbar, container, 0) // Scroll to top
            }
            "back" -> player.clear("retrieve_more")
            else -> if (!player.inventory.add(item.id)) {
                player.inventoryFull()
            }
        }
        refreshItems(player)
    }

    @Despawn
    fun despawn(player: Player) {
        // Don't want to store in account save
        player.inventories.clear("diangos_item_retrieval")
    }

    @Close("diangos_item_retrieval")
    fun close(player: Player, id: String) {
        player.inventories.clear(id)
    }

    @Open("diangos_item_retrieval")
    fun refreshItems(player: Player) {
        val more: Boolean = player["retrieve_more", false]
        player.inventories.clear("diangos_item_retrieval")
        val inventory = player.inventories.inventory("diangos_item_retrieval")
        val definition = inventoryDefinitions.get("diangos_item_retrieval")
        var displayMore = false
        inventory.transaction {
            clear()
            // Add back "button" when displaying excess
            if (more) {
                add("back")
            }
            var skipped = 0
            for (index in 0 until inventory.size) {
                val id = definition.ids?.getOrNull(index) ?: continue
                val itemDefinition = itemDefinitions.get(id)
                val event: String? = itemDefinition.getOrNull("event")
                if ((event == null || player[event, false]) && !player.ownsItem(itemDefinition.stringId)) {
                    // Add second screen if itemLimit is reached
                    if (!more && inventory.count >= itemLimit) {
                        displayMore = true
                        break
                    }
                    // If displaying second screen skip first X items
                    if (more && skipped++ < itemLimit) {
                        continue
                    }
                    add(itemDefinition.stringId)
                }
            }
            // Add more "button" when too many to display
            if (displayMore) {
                add("more")
            }
        }
        player.interfaceOptions.unlockAll("diangos_item_retrieval", "items", 0..inventory.count)
        player.sendInventory("diangos_item_retrieval")
        if (displayMore) {
            player.sendScript("scrollbar_vertical", scrollbar, container)
            player.sendScript("set_scroll_height", scrollbar, container, 300, 0)
            player.sendScript("interface_inv_init", container, 453, 8, 7, 0, -1, "Claim", "", "", "", "")
        } else {
            player.sendScript("interface_inv_init", container, 453, 8, 6, 0, -1, "Claim", "", "", "", "")
        }
    }
}

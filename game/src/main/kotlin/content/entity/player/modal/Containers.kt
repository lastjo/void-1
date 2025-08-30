package content.entity.player.modal

import world.gregs.voidps.engine.client.sendInterfaceItemUpdate
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Subscribe

class Containers(
    private val itemDefinitions: ItemDefinitions,
    private val inventoryDefinitions: InventoryDefinitions
) {
    @Subscribe("inventory_update")
    fun sendUpdates(player: Player, inventory: String) {
        val secondary = inventory.startsWith("_")
        val id = if (secondary) inventory.removePrefix("_") else inventory
        val updates = player.inventories.inventory(inventory).transaction.changes.itemChanges
        player.sendInterfaceItemUpdate(
            key = inventoryDefinitions.get(id).id,
            updates = updates.map { Triple(it.index, itemDefinitions.getOrNull(it.item.id)?.id ?: -1, it.item.amount) },
            secondary = secondary,
        )
    }
}

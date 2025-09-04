package content.entity.player.modal

import world.gregs.voidps.engine.client.sendInterfaceItemUpdate
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.InventoryUpdated

class Containers(
    private val itemDefinitions: ItemDefinitions,
    private val inventoryDefinitions: InventoryDefinitions,
) {

    @InventoryUpdated
    fun sendUpdates(player: Player, id: String) {
        val secondary = id.startsWith("_")
        val actualId = if (secondary) id.removePrefix("_") else id
        val updates = player.inventories.instances[actualId]?.transaction?.changes?.changes ?: return
        player.sendInterfaceItemUpdate(
            key = inventoryDefinitions.get(actualId).id,
            updates = updates.map { Triple(it.index, itemDefinitions.getOrNull(it.item.id)?.id ?: -1, it.item.amount) },
            secondary = secondary,
        )
    }
}

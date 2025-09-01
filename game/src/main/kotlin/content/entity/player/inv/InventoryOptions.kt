package content.entity.player.inv

import com.github.michaelbull.logging.InlineLogger
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.combat.CombatMovement
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.engine.inv.swap
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Refresh
import world.gregs.voidps.type.sub.Swap

class InventoryOptions {

    val logger = InlineLogger()

    @Refresh("inventory")
    fun refresh(player: Player, id: String) {
        player.interfaceOptions.unlockAll(id, "inventory", 0 until 28)
        player.interfaceOptions.unlock(id, "inventory", 28 until 56, "Drag")
        player.sendInventory(id)
    }

    @Swap
    fun swap(player: Player) {
        player.queue.clearWeak()
    }

    @Swap("inventory")
    fun swap(player: Player, fromSlot: Int, toSlot: Int) {
        player.closeInterfaces()
        if (player.mode is CombatMovement) {
            player.mode = EmptyMode
        }
        if (!player.inventory.swap(fromSlot, toSlot)) {
            logger.info { "Failed switching interface items $this" }
        }
    }

    @Interface(component = "inventory", id = "inventory")
    fun option(player: Player, item: Item, itemSlot: Int, optionIndex: Int, id: String) {
        val itemDef = item.def
        val equipOption = when (optionIndex) {
            6 -> itemDef.options.getOrNull(3)
            7 -> itemDef.options.getOrNull(4)
            9 -> "Examine"
            else -> itemDef.options.getOrNull(optionIndex)
        }
        if (equipOption == null) {
            logger.info { "Unknown item option $item $optionIndex" }
            return
        }
        player.closeInterfaces()
        if (player.mode is CombatMovement) {
            player.mode = EmptyMode
        }
        Publishers.launch {
            Publishers.all.inventoryOption(player, item, id, equipOption, itemSlot)
        }
        player.emit(
            InventoryOption(
                player,
                id,
                item,
                itemSlot,
                equipOption,
            ),
        )
    }
}

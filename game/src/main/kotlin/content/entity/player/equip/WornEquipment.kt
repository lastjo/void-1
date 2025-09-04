package content.entity.player.equip

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.inv.InventoryOption
import world.gregs.voidps.cache.definition.data.ItemDefinition
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Refresh

class WornEquipment {

    val logger = InlineLogger()

    @Refresh("worn_equipment")
    fun refresh(player: Player, id: String) {
        player.sendInventory(id)
    }

    @Interface("Show Equipment Stats", "bonuses", "worn_equipment")
    fun showStats(player: Player) {
        player["equipment_bank_button"] = false
        player.open("equipment_bonuses")
    }

    @Interface("Show Price-checker", "price", "worn_equipment")
    fun showPriceChecker(player: Player) {
        player.open("price_checker")
    }

    @Interface("Show Items Kept on Death", "items", "worn_equipment")
    fun showItemsKept(player: Player) {
        player.open("items_kept_on_death")
    }

    @Interface(component = "*_slot", id = "worn_equipment")
    suspend fun option(player: Player, item: Item, optionIndex: Int, id: String, component: String) {
        val equipOption = getEquipmentOption(item.def, optionIndex)
        if (equipOption == null) {
            logger.info { "Unhandled equipment option $item - $optionIndex" }
            return
        }
        val slot = EquipSlot.by(component.removeSuffix("_slot"))
        player.closeInterfaces()
        Publishers.all.inventoryOption(player, item, id, equipOption, slot.index)
        player.emit(InventoryOption(player, id, item, slot.index, equipOption))
    }

    fun getEquipmentOption(itemDef: ItemDefinition, optionId: Int): String? {
        val equipOption: String? = itemDef.getOrNull<Map<Int, String>>("worn_options")?.get(optionId - 1)
        if (equipOption != null) {
            return equipOption
        }
        return when (optionId) {
            0 -> "Remove"
            9 -> "Examine"
            else -> null
        }
    }
}

package content.entity.npc.shop.stock

import content.entity.npc.shop.shopInventory
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.menu
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.InventorySlotChanged

class ItemInformation {

    /**
     * The item information side panel which shows a shop items requirements, stats and price
     */
    @Interface("Info", id = "shop")
    fun info(player: Player, component: String, itemSlot: Int) {
        val sample = component == "sample"
        val actualIndex = itemSlot / (if (sample) 4 else 6)
        val inventory = player.shopInventory(sample)
        val item = inventory[actualIndex]
        player["info_sample"] = sample
        player["info_index"] = actualIndex
        val price = when {
            sample -> -1
            item.amount < 1 -> item.amount
            else -> Price.getPrice(player, item.id, actualIndex, item.amount)
        }
        ItemInfo.showInfo(player, item, price)
    }

    @Interface("Close", "exit", "item_info")
    fun close(player: Player) {
        when (player.menu) {
            "shop" -> {
                player.open("shop_side")
                player.interfaceOptions.send("shop_side", "inventory")
            }
            "grand_exchange" -> player.close("item_info")
        }
    }

    @InventorySlotChanged
    fun update(player: Player, inventory: String, item: Item, itemSlot: Int) {
        if (player.interfaces.contains("item_info")) {
            player.sendScript("refresh_item_info")
        }
        if (!player.contains("info_sample")) {
            return
        }
        val shop: String = player["shop"] ?: return
        val index: Int = player["info_index"] ?: return
        if (inventory == shop && itemSlot == index) {
            player["item_info_price"] = if (item.amount == 0) 0 else Price.getPrice(player, item.id, index, item.amount)
        }
    }
}

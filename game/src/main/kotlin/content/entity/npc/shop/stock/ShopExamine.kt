package content.entity.npc.shop.stock

import content.entity.npc.shop.shopInventory
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Interface

class ShopExamine {

    @Interface("Examine", "inventory", "shop_side")
    fun side(player: Player, item: Item) {
        val examine: String = item.def.getOrNull("examine") ?: return
        player.message(examine)
    }

    @Interface("Examine", "sample", "shop")
    fun sample(player: Player, itemSlot: Int) {
        val item = player.shopInventory(true)[itemSlot / 4]
        val examine: String = item.def.getOrNull("examine") ?: return
        player.message(examine)
    }

    @Interface("Examine", "stock", "shop")
    fun stock(player: Player, itemSlot: Int) {
        val item = player.shopInventory(false)[itemSlot / 6]
        val examine: String = item.def.getOrNull("examine") ?: return
        player.message(examine)
    }
}

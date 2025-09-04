package content.skill.cooking

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.sub.Inventory

class Empty {

    @Inventory("Empty")
    fun empty(player: Player, item: Item, itemSlot: Int) {
        val replacement: String = item.def.getOrNull("empty") ?: return
        player.inventory.replace(itemSlot, item.id, replacement)
        player.message("You empty the ${item.def.name.substringBefore(" (").lowercase()}.", ChatType.Filter)
    }

    @Inventory("Empty Dish")
    fun pieDish(player: Player, item: Item, itemSlot: Int) {
        player.inventory.replace(itemSlot, item.id, "pie_dish")
        player.message("You remove the burnt pie from the pie dish.", ChatType.Filter)
    }
}

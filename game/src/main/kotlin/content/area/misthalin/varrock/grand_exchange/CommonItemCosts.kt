package content.area.misthalin.varrock.grand_exchange

import content.social.trade.exchange.GrandExchange
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.chat.toDigitGroupString
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class CommonItemCosts(
    private val enums: EnumDefinitions,
    private val exchange: GrandExchange,
    private val itemDefinitions: ItemDefinitions,
) {

    @Open("common_item_costs")
    fun open(player: Player) {
        val type = player["common_item_costs", "ores"]
        val enum = enums.get("exchange_items_$type")
        var index = 1
        for (i in 0 until enum.length) {
            val item = enum.getInt(i)
            val definition = itemDefinitions.get(item)
            val price = exchange.history.marketPrice(definition.stringId)
            player.sendScript("send_common_item_price", index, i, "${price.toDigitGroupString()} gp")
            index += 2
        }
        player.interfaceOptions.unlockAll("common_item_costs", "items", 0..enum.length * 2)
    }

    @Interface("Examine", "items", "common_item_costs")
    fun examine(player: Player, item: Item) {
        player.message(item.def.getOrNull("examine") ?: return)
    }
}

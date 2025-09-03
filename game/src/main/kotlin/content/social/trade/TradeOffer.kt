package content.social.trade

import content.entity.player.dialogue.type.intEntry
import content.social.trade.Trade.isTrading
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.restrict.ItemRestrictionRule
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItemLimit.removeToLimit
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Spawn

/**
 * Offering an item to trade or loan
 */
class TradeOffer(private val definitions: ItemDefinitions) {

    private val tradeRestriction = object : ItemRestrictionRule {
        override fun restricted(id: String): Boolean {
            val def = definitions.get(id)
            return def.lendTemplateId != -1 || def.dummyItem != 0 || !def["tradeable", true]
        }
    }

    @Spawn
    fun spawn(player: Player) {
        player.offer.itemRule = tradeRestriction
    }

    @Interface(component = "offer", id = "trade_side")
    suspend fun offer(player: Player, item: Item, option: String) {
        val amount = when (option) {
            "Offer" -> 1
            "Offer-5" -> 5
            "Offer-10" -> 10
            "Offer-All" -> Int.MAX_VALUE
            "Offer-X" -> player.intEntry("Enter amount:")
            else -> return
        }
        offer(player, item.id, amount)
    }

    @Interface("Value", "offer", "trade_side")
    fun value(player: Player, item: Item) {
        player.message("${item.def.name} is priceless!", ChatType.Trade)
    }

    // Item must be tradeable and not lent or a dummy item
    fun offer(player: Player, id: String, amount: Int) {
        if (!isTrading(player, amount)) {
            return
        }
        val offered = player.inventory.transaction {
            val added = removeToLimit(id, amount)
            val transaction = link(player.offer)
            transaction.add(id, added)
        }
        if (!offered) {
            player.message("That item is not tradeable.")
        }
    }
}

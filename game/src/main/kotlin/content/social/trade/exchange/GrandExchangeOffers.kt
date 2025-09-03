package content.social.trade.exchange

import com.github.michaelbull.logging.InlineLogger
import content.entity.npc.shop.stock.ItemInfo
import content.entity.player.bank.isNote
import content.entity.player.bank.noted
import content.entity.player.modal.Tab
import content.entity.player.modal.tab
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.type.sub.*
import kotlin.math.ceil

class GrandExchangeOffers(
    private val exchange: GrandExchange,
    private val itemDefinitions: ItemDefinitions,
) {

    val logger = InlineLogger()

    @Spawn
    fun spawn(player: Player) {
        exchange.login(player)
    }

    @Open("grand_exchange")
    fun open(player: Player, id: String) {
        player.sendVariable("grand_exchange_ranges")
        player["grand_exchange_page"] = "offers"
        player["grand_exchange_box"] = -1
        player.interfaceOptions.unlockAll(id, "collect_slot_0")
        player.interfaceOptions.unlockAll(id, "collect_slot_1")
        for (i in 0 until 6) {
            exchange.refresh(player, i)
        }
    }

    @Close("grand_exchange")
    fun close(player: Player) {
        GrandExchange.clearSelection(player)
    }

    /*
        Offers
     */

    @Interface("Make Offer", "view_offer_*", "grand_exchange")
    fun offer(player: Player, component: String) {
        val slot = component.removePrefix("view_offer_").toInt()
        if (slot > 1 && !World.members) {
            return
        }
        val offer = player.offers.getOrNull(slot) ?: return
        player["grand_exchange_box"] = slot
        selectItem(player, offer.item)
    }

    @Interface("Make Buy Offer", "buy_offer_*", "grand_exchange")
    fun buyOffer(player: Player, component: String) {
        val slot = component.removePrefix("buy_offer_").toInt()
        if (slot > 1 && !World.members) {
            return
        }
        player["grand_exchange_box"] = slot
        player["grand_exchange_page"] = "buy"
        player["grand_exchange_item_id"] = -1
        openItemSearch(player)
    }

    fun chooseItem(player: Player, component: String) {
        val slot = component.removePrefix("buy_offer_").toInt()
        if (slot > 1 && !World.members) {
            return
        }
        player["grand_exchange_box"] = slot
        player["grand_exchange_page"] = "buy"
        player["grand_exchange_item_id"] = -1
        openItemSearch(player)
    }

    @Continue
    fun offer(player: Player, item: Item) {
        val def = itemDefinitions.getOrNull(item.id)
        if (def == null || !def.exchangeable || def.noted || def.lent || def.dummyItem != 0) {
            player.message("You can't trade that item on the Grand Exchange.")
            return
        }
        selectItem(player, item.id)
        player["grand_exchange_price"] = player["grand_exchange_market_price", 0]
        ItemInfo.showInfo(player, item)
    }

    @Interface("Make Sell Offer", "sell_offer_*", "grand_exchange")
    fun sellOffer(player: Player, component: String) {
        val slot = component.removePrefix("sell_offer_").toInt()
        if (slot > 1 && !World.members) {
            return
        }
        player["grand_exchange_box"] = slot
        player["grand_exchange_page"] = "sell"
        player.open("stock_side")
        player["grand_exchange_item_id"] = -1
    }

    @Open("stock_side")
    fun openSide(player: Player, id: String) {
        player.tab(Tab.Inventory)
        player.interfaceOptions.send(id, "items")
        player.interfaceOptions.unlockAll(id, "items", 0 until 28)
        player.sendInventory(player.inventory)
        player.sendScript("grand_exchange_hide_all")
    }

    @Interface("Offer", "items", "stock_side")
    fun offerItem(player: Player, item: Item) {
        val resolved = if (item.isNote) item.noted else item
        if (resolved == null) {
            logger.warn { "Issue selling noted item on GE: $item" }
            return
        }
        val def = resolved.def
        if (!def.exchangeable || def.noted || def.lent || def.dummyItem != 0) {
            player.message("You can't trade that item on the Grand Exchange.")
            return
        }
        selectItem(player, resolved.id)
        player["grand_exchange_quantity"] = resolved.amount
        player["grand_exchange_price"] = player["grand_exchange_market_price", 0]
    }

    /*
        Buy Offer
     */

    @Interface("Choose Item", "choose_item", "grand_exchange")
    fun openItemSearch(player: Player) {
        player.open("grand_exchange_item_dialog")
        player.sendScript("item_dialogue_reset", "Grand Exchange Item Search")
    }

    /*
        Sell Offer
     */

    fun selectItem(player: Player, item: String) {
        val definition = itemDefinitions.get(item)
        player["grand_exchange_item"] = item
        player["grand_exchange_item_id"] = definition.id
        player.interfaces.sendText("grand_exchange", "examine", definition["examine", ""])
        val price = exchange.history.marketPrice(item)
        player["grand_exchange_market_price"] = price
        player["grand_exchange_range_min"] = ceil(price * 0.95).toInt()
        player["grand_exchange_range_max"] = ceil(price * 1.05).toInt()
    }
}

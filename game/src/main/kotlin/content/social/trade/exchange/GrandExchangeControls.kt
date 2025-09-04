package content.social.trade.exchange

import content.entity.player.bank.noted
import content.entity.player.dialogue.type.intEntry
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import kotlin.math.ceil

class GrandExchangeControls {

    @Open("grand_exchange")
    fun open(player: Player) {
        /*
           This is a hacky way of converting between original and newer ui (limited price range vs unlimited with +/-5%)
           It doesn't account for hover and tooltip changes or location of buttons and isn't the most responsive as
           it's limited by the speed variables change by the existing (old) cs2.
         */
        val limit = Settings["grandExchange.priceLimit", true]
        player.interfaces.sendVisibility("grand_exchange", "price_range_min", limit)
        player.interfaces.sendVisibility("grand_exchange", "price_range_max", limit)
        player.interfaces.sendVisibility("grand_exchange", "price_range", limit)
        player.interfaces.sendVisibility("grand_exchange", "offer_min_sprite", limit)
        player.interfaces.sendVisibility("grand_exchange", "offer_max_sprite", limit)
        player.interfaces.sendText("grand_exchange", "offer_min", if (limit) "" else "-5%")
        player.interfaces.sendText("grand_exchange", "offer_max", if (limit) "" else "+5%")
    }

    @Interface("Add *", "add_*", "grand_exchange")
    suspend fun add(player: Player, component: String) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        when (player["grand_exchange_page", "offers"]) {
            "sell" -> {
                val total = totalItems(player)
                player["grand_exchange_quantity"] = when (component) {
                    "add_1" -> 1
                    "add_10" -> 10
                    "add_100" -> 100
                    "add_all" -> total
                    else -> return
                }.coerceAtMost(total)
            }
            "buy" -> when (component) {
                "add_1" -> player.inc("grand_exchange_quantity", 1)
                "add_10" -> player.inc("grand_exchange_quantity", 10)
                "add_100" -> player.inc("grand_exchange_quantity", 100)
                "add_all" -> player.inc("grand_exchange_quantity", 1000)
                else -> return
            }
        }
    }

    @Interface("Edit Quantity", "add_x", "grand_exchange")
    suspend fun editQuantity(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        player["grand_exchange_quantity"] = when (player["grand_exchange_page", "offers"]) {
            "sell" -> player.intEntry("Enter the amount you wish to sell:").coerceAtMost(totalItems(player))
            "buy" -> player.intEntry("Enter the amount you wish to purchase:")
            else -> return
        }
    }

    @Interface("Increase Quantity", "increase_quantity", "grand_exchange")
    fun increaseQuantity(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        if (player["grand_exchange_quantity", 0] < Int.MAX_VALUE - 1) {
            player["grand_exchange_quantity"] = (player["grand_exchange_quantity", 0] + 1).coerceAtMost(
                when (player["grand_exchange_page", "offers"]) {
                    "sell" -> totalItems(player)
                    "buy" -> Int.MAX_VALUE
                    else -> return
                },
            )
        }
    }

    @Interface("Decrease Quantity", "decrease_quantity", "grand_exchange")
    fun decreaseQuantity(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        if (player.dec("grand_exchange_quantity", 1) < 0) {
            player["grand_exchange_quantity"] = 0
        }
    }

    @Interface("Increase Price", "increase_price", "grand_exchange")
    fun increasePrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        val limit = if (Settings["grandExchange.priceLimit", true]) player["grand_exchange_range_max", 0] else Int.MAX_VALUE
        player["grand_exchange_price"] = (player["grand_exchange_price", 0] + 1L).coerceAtMost(limit.toLong()).toInt()
        if (!Settings["grandExchange.priceLimit", true]) {
            updateLimits(player)
        }
    }

    @Interface("Decrease Price", "decrease_price", "grand_exchange")
    fun decreasePrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        val limit = if (Settings["grandExchange.priceLimit", true]) player["grand_exchange_range_min", 0] else 0
        player["grand_exchange_price"] = (player["grand_exchange_price", 0] - 1).coerceAtLeast(limit)
        if (!Settings["grandExchange.priceLimit", true]) {
            updateLimits(player)
        }
    }

    @Interface("Offer Market Price", "offer_market", "grand_exchange")
    fun marketPrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        if (!Settings["grandExchange.priceLimit", true] && player.hasClock("grand_exchange_price_delay")) {
            return
        }
        player.start("grand_exchange_price_delay", 1)
        player["grand_exchange_price"] = player["grand_exchange_market_price", 0]
        if (!Settings["grandExchange.priceLimit", true]) {
            updateLimits(player)
        }
    }

    @Interface("Edit Price", "offer_x", "grand_exchange")
    suspend fun editPrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        val min = if (Settings["grandExchange.priceLimit", true]) player["grand_exchange_range_min", 0] else 0
        val max = if (Settings["grandExchange.priceLimit", true]) player["grand_exchange_range_max", 0] else Int.MAX_VALUE
        player["grand_exchange_price"] = when (player["grand_exchange_page", "offers"]) {
            "sell" -> player.intEntry("Enter the price you wish to sell for:")
            "buy" -> player.intEntry("Enter the price you wish to buy for:")
            else -> return
        }.coerceIn(min, max)
        if (!Settings["grandExchange.priceLimit", true]) {
            updateLimits(player)
        }
    }

    @Interface("Offer Minimum Price", "offer_min", "grand_exchange")
    fun minPrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        if (Settings["grandExchange.priceLimit", true]) {
            player["grand_exchange_price"] = player["grand_exchange_range_min", 0]
        } else {
            if (player.hasClock("grand_exchange_price_delay")) {
                return
            }
            player.start("grand_exchange_price_delay", 1)
            val price = player["grand_exchange_market_price", 0]
            val percent = ceil(price * 0.05).toInt()
            player["grand_exchange_price"] = (player["grand_exchange_price", 0] - percent).coerceAtLeast(0)
            updateLimits(player)
        }
    }

    @Interface("Offer Maximum Price", "offer_max", "grand_exchange")
    fun maxPrice(player: Player) {
        if (!itemSelected(player)) {
            return
        }
        player.closeDialogue()
        if (Settings["grandExchange.priceLimit", true]) {
            player["grand_exchange_price"] = player["grand_exchange_range_max", 0]
        } else {
            if (player.hasClock("grand_exchange_price_delay")) {
                return
            }
            player.start("grand_exchange_price_delay", 1)
            val price = player["grand_exchange_market_price", 0]
            val percent = ceil(price * 0.05).toInt()
            player["grand_exchange_price"] = (player["grand_exchange_price", 0] + percent).coerceAtMost(Int.MAX_VALUE)
            updateLimits(player)
        }
    }

    fun totalItems(player: Player): Int {
        val item = Item(player["grand_exchange_item", ""])
        val noted = item.noted
        var total = 0
        if (noted != null) {
            total += player.inventory.count(noted.id)
        }
        total += player.inventory.count(item.id)
        return total
    }

    /**
     * We have to update the min and max range everytime the price changes to avoid the price conflicting with cs2
     */
    fun updateLimits(player: Player) {
        val price = player["grand_exchange_market_price", 0]
        val percent = ceil(price * 0.05).toInt()
        player["grand_exchange_range_min"] = (player["grand_exchange_price", 0] - percent).coerceAtLeast(0)
        player["grand_exchange_range_max"] = (player["grand_exchange_price", 0] + percent).coerceAtMost(Int.MAX_VALUE)
    }

    fun itemSelected(player: Player): Boolean {
        if (player["grand_exchange_item_id", -1] == -1) {
            // https://youtu.be/wAtBnxSxgiA?si=jsurs070eip_6INS&t=191
            player.message("You must choose an item first.")
            return false
        }
        return true
    }
}

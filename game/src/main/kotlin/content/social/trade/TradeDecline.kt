package content.social.trade

import content.social.trade.Trade.getPartner
import content.social.trade.Trade.isTradeInterface
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.InterfaceOption
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.menu
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.playerDespawn
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Interface

/**
 * Declining or closing cancels the trade
 */
class TradeDecline {

    @Interface("Decline", "decline", "trade_main")
    @Interface("Decline", "decline", "trade_confirm")
    @Interface("Close", "close", "trade_main")
    @Interface("Close", "close", "trade_confirm")
    fun decline(player: Player) {
        val other = getPartner(player)
        player.message("Declined trade.", ChatType.Trade)
        other?.message("Other player declined trade.", ChatType.Trade)
        player.closeMenu()
        other?.closeMenu()
    }

    @Despawn
    fun despawn(player: Player) {
        if (isTradeInterface(player.menu)) {
            val other = getPartner(player)
            player.closeMenu()
            other?.message("Other player declined trade.", ChatType.Trade)
            other?.closeMenu()
        }
    }

}

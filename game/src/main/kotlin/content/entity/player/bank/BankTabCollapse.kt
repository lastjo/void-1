package content.entity.player.bank

import content.entity.player.bank.Bank.tabIndex
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.transact.operation.ShiftItem.shiftToFreeIndex
import world.gregs.voidps.type.sub.Interface

class BankTabCollapse {

    @Interface("Collapse", "tab_*", "bank")
    fun collapse(player: Player, component: String) {
        val tab = component.removePrefix("tab_").toInt() - 1
        val tabIndex = tabIndex(player, tab)
        val count: Int = player["bank_tab_$tab", 0]
        val collapsed = player.bank.transaction {
            repeat(count) {
                shiftToFreeIndex(tabIndex)
            }
        }
        if (collapsed) {
            repeat(count) {
                Bank.decreaseTab(player, tab)
            }
        }
    }
}

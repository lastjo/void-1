package content.entity.player.bank

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.shift
import world.gregs.voidps.engine.inv.swap
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.InventoryUpdated
import world.gregs.voidps.type.sub.Swap

class BankTabs {

    @InventoryUpdated("bank")
    fun update(player: Player) {
        player["bank_spaces_used_free"] = player.bank.countFreeToPlayItems()
        player["bank_spaces_used_member"] = player.bank.count
    }

    @Swap("bank", "inventory")
    fun swap(player: Player, fromSlot: Int, toSlot: Int) {
        when (player["bank_item_mode", "swap"]) {
            "swap" -> player.bank.swap(fromSlot, toSlot)
            "insert" -> {
                val fromTab = Bank.getTab(player, fromSlot)
                val toTab = Bank.getTab(player, toSlot)
                shiftTab(player, fromSlot, toSlot, fromTab, toTab)
            }
        }
    }

    @Interface("View all", "tab_1", "bank")
    fun viewAll(player: Player) {
        player["open_bank_tab"] = 1
    }

    @Interface("View Tab", "tab_*", "bank")
    fun viewTab(player: Player, component: String) {
        player["open_bank_tab"] = component.removePrefix("tab_").toInt()
    }

    @Interface("Toggle swap/insert", "item_mode", "bank")
    fun toggleMode(player: Player, component: String) {
        val value: String = player["bank_item_mode", "swap"]
        player["bank_item_mode"] = if (value == "insert") "swap" else "insert"
    }

    @Swap("bank", "inventory", toComponent = "tab_*")
    fun swapTab(player: Player, fromSlot: Int, toComponent: String) {
        val fromTab = Bank.getTab(player, fromSlot)
        val toTab = toComponent.removePrefix("tab_").toInt() - 1
        val toIndex = if (toTab == Bank.MAIN_TAB) player.bank.freeIndex() else Bank.tabIndex(player, toTab + 1)
        shiftTab(player, fromSlot, toIndex, fromTab, toTab)
    }

    fun Inventory.countFreeToPlayItems(): Int = items.count { it.isNotEmpty() && !it.def.members }

    /*
        Move to index in same tab -> shiftInsert
     */
    fun shiftTab(player: Player, fromIndex: Int, toIndex: Int, fromTab: Int, toTab: Int) {
        val moved = fromTab != toTab
        // Increase count of target tab
        if (moved && toTab > 0) {
            player.inc("bank_tab_$toTab")
        }
        if (moved || toTab == Bank.MAIN_TAB) {
            Bank.decreaseTab(player, fromTab)
        }
        // Remove one from target index to include this item's own position change
        player.bank.shift(fromIndex, if (fromIndex < toIndex) toIndex - 1 else toIndex)
    }
}

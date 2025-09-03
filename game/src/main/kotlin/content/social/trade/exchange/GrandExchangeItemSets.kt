package content.social.trade.exchange

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.modal.Tab
import content.entity.player.modal.tab
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.interact.itemOnNPCApproach
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.sendInventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.UseOn

class GrandExchangeItemSets(private val enumDefinitions: EnumDefinitions) {

    val logger = InlineLogger()

    @Open("exchange_item_sets")
    fun open(player: Player) {
        player.open("exchange_sets_side")
        player.sendScript("grand_exchange_sets")
        player.interfaceOptions.unlockAll("exchange_item_sets", "sets", 0..113)
    }

    @Close("exchange_item_sets")
    fun close(player: Player) {
        player.close("exchange_sets_side")
    }

    @Interface("Components", "sets", "exchange_item_sets")
    fun components(player: Player, item: Item) {
        val descriptions = enumDefinitions.get("exchange_set_descriptions")
        player.message(descriptions.getString(item.def.id))
    }

    @Interface("Exchange", "sets", "exchange_item_sets")
    fun exchange(player: Player, item: Item) {
        val components: List<String> = item.def.getOrNull("items") ?: return
        player.inventory.transaction {
            for (itm in components) {
                remove(itm)
            }
            add(item.id)
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Deficient -> {
                //             https://youtu.be/Tz2jgdj1bWg?si=PQz8E4H2bPoBlAfA&t=94
                player.message("You don't have the parts that make up this set.")
            }
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> {
                //            https://youtu.be/FfVilurxzj0?si=wnz1ujXs_Xomfzmu&t=39
                player.message("You successfully traded your item components for a set!")
            }
            TransactionError.Invalid -> logger.warn { "Invalid set exchange for item ${item.id} $components" }
        }
    }

    @Interface("Examine", "items", "exchange_sets_side")
    @Interface("Examine", "items", "exchange_item_sets")
    fun examine(player: Player, item: Item) {
        player.message(item.def.getOrNull("examine") ?: return)
    }

    /*
        Side
     */

    @Open("exchange_sets_side")
    fun openSide(player: Player, id: String) {
        player.tab(Tab.Inventory)
        player.interfaceOptions.send(id, "items")
        player.interfaceOptions.unlockAll(id, "items", 0 until 28)
        player.sendInventory(player.inventory)
    }

    @Interface("Components", "items", "exchange_sets_side")
    fun itemComponents(player: Player, item: Item) {
        val descriptions = enumDefinitions.get("exchange_set_descriptions")
        val text = descriptions.getString(item.def.id)
        if (text != "shop_dummy") {
            player.message(text)
        } else {
            player.message("That isn't a set item.")
        }
    }

    @UseOn(on = "grand_exchange_clerk*")
    suspend fun use(player: Player, target: NPC, item: Item, itemSlot: Int) {
        player.approachRange(2)
        exchangeSet(player, item, itemSlot)
    }

    @Interface("Exchange", "items", "exchange_sets_side")
    fun exchangeSet(player: Player, item: Item, itemSlot: Int) {
        val components: List<String>? = item.def.getOrNull("items")
        if (components == null) {
            player.message("That isn't a set item, you can't break it up into component parts.")
            return
        }
        player.inventory.transaction {
            remove(itemSlot, item.id)
            for (component in components) {
                add(component)
            }
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull("for the component parts")
            TransactionError.None -> {
                //            https://youtu.be/FfVilurxzj0?si=wnz1ujXs_Xomfzmu&t=39
                player.message("You successfully traded your set for its component items!")
            }
            else -> logger.warn { "${player.inventory.transaction.error} set exchange for item ${item.id} $components" }
        }
    }
}

package content.skill.farming

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.inv.inventoryItem
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnItem
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.inv.transact.operation.RemoveItemLimit.removeToLimit
import world.gregs.voidps.engine.inv.transact.operation.ReplaceItem.replace
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class Sack {

    private data class Vegetable(val id: String, val name: String, val plural: String, val description: String = "a $name")

    private val vegetables = mapOf(
        "raw_potato" to Vegetable("raw_potato", "potato", "potatoes"),
        "onion" to Vegetable("onion", "onion", "onions", "an onion"),
        "cabbage" to Vegetable("cabbage", "cabbage", "cabbages"),
    )

    val logger = InlineLogger()

    @Inventory("Fill", "empty_sack")
    fun fillSack(player: Player, item: Item, itemSlot: Int) {
        var index = -1
        for (id in vegetables.keys) {
            index = player.inventory.indexOf(id)
            if (index != -1) {
                break
            }
        }

        if (index == -1) {
            player.message("You don't have any potatoes, onions or cabbages.")
            return
        }
        val item = player.inventory[index]
        val veg = vegetables[item.id]
        if (veg == null) {
            player.message("You don't have any potatoes, onions or cabbages.")
            return
        }
        player.inventory.transaction {
            val removed = removeToLimit(item.id, 10)
            if (removed == 0) {
                error = TransactionError.Deficient(0)
            }
            replace(itemSlot, "empty_sack", "${veg.plural}_$removed")
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Deficient -> player.message("You don't have any potatoes, onions or cabbages.")
            TransactionError.None -> {}
            else -> logger.warn { "Error filling sack" }
        }
    }

    @Inventory("Fill", "potatoes_*")
    @Inventory("Fill", "onions_*")
    @Inventory("Fill", "cabbages_*")
    fun fill(player: Player, item: Item, itemSlot: Int) {
        val veg = vegetables[item.id] ?: return
        fill(player, item, itemSlot, veg)
    }

    private fun fill(player: Player, item: Item, itemSlot: Int, veg: Vegetable) {
        val current = item.id.removePrefix("${veg.plural}_").toInt()
        player.inventory.transaction {
            val removed = removeToLimit(item.id, 10 - current)
            if (removed == 0) {
                error = TransactionError.Deficient(0)
            }
            replace(itemSlot, item.id, "${veg.plural}_${current + removed}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Deficient -> {}
            TransactionError.None -> {}
            else -> logger.warn { "Error filling ${veg.plural}." }
        }
    }

    @Inventory("Fill", "potatoes_10")
    @Inventory("Fill", "onions_10")
    @Inventory("Fill", "cabbages_10")
    fun full(player: Player, item: Item) {
        val veg = vegetables[item.id] ?: return
        player.message("The ${veg.name} sack is already full.")
    }

    @UseOn("potato", "potatoes_10")
    @UseOn("onion", "onions_10")
    @UseOn("cabbage", "cabbages_10")
    fun full(player: Player, fromItem: Item, toItem: Item) {
        val veg = vegetables[fromItem.id] ?: return
        player.message("The ${veg.name} sack is already full.")
    }

    @UseOn("potato", "potatoes_*")
    @UseOn("onion", "onions_*")
    @UseOn("cabbage", "cabbages_*")
    fun fill(player: Player, fromItem: Item, fromSlot: Int, toItem: Item) {
        val veg = vegetables[fromItem.id] ?: return
        fill(player, fromItem, fromSlot, veg)
    }

    @Inventory("Remove-one", "potatoes_*")
    @Inventory("Remove-one", "onions_*")
    @Inventory("Remove-one", "cabbages_*")
    fun removeOne(player: Player, item: Item, itemSlot: Int) {
        val veg = vegetables.values.firstOrNull { item.id.startsWith(it.plural) } ?: return
        val current = item.id.removePrefix("${veg.plural}_").toInt()

        player.inventory.transaction {
            add(veg.id)
            replace(itemSlot, item.id, if (current == 1) "empty_sack" else "${veg.plural}_${current - 1}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> player.message("You take ${veg.description} out of the ${veg.name} sack.")
            else -> logger.warn { "Error emptying ${veg.description}." }
        }
    }

    @Inventory("Empty", "potatoes_*")
    @Inventory("Empty", "onions_*")
    @Inventory("Empty", "cabbages_*")
    fun empty(player: Player, item: Item, itemSlot: Int) {
        val veg = vegetables.values.firstOrNull { item.id.startsWith(it.plural) } ?: return
        val current = item.id.removePrefix("${veg.plural}_").toInt()

        player.inventory.transaction {
            val added = addToLimit(veg.id, current)
            if (added == 0) {
                error = TransactionError.Full(0)
            }
            replace(itemSlot, item.id, if (added == current) "empty_sack" else "${veg.plural}_${current - added}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> {}
            else -> logger.warn { "Error emptying ${veg.plural}." }
        }
    }

}

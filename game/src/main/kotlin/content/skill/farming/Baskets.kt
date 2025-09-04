package content.skill.farming

import com.github.michaelbull.logging.InlineLogger
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.inv.transact.operation.RemoveItemLimit.removeToLimit
import world.gregs.voidps.engine.inv.transact.operation.ReplaceItem.replace
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class Baskets {

    val logger = InlineLogger()

    private data class Fruit(val id: String, val name: String, val plural: String, val description: String = "a $name")

    private val fruit = mapOf(
        "strawberry" to Fruit("strawberry", "strawberry", "strawberries"),
        "orange" to Fruit("orange", "orange", "oranges", "an orange"),
        "banana" to Fruit("banana", "banana", "bananas"),
        "cooking_apple" to Fruit("cooking_apple", "apple", "apples", "an apple"),
        "tomato" to Fruit("tomato", "tomato", "tomatoes"),
    )

    @Inventory("Fill", "basket")
    fun fill(player: Player, itemSlot: Int) {
        var index = -1
        for (id in fruit.keys) {
            index = player.inventory.indexOf(id)
            if (index != -1) {
                break
            }
        }

        if (index == -1) {
            player.message("You don't have any fruit with which to fill the basket.")
            return
        }
        val item = player.inventory[index]
        val veg = fruit[item.id]
        if (veg == null) {
            player.message("You don't have any fruit with which to fill the basket.")
            return
        }
        player.inventory.transaction {
            val removed = removeToLimit(item.id, 5)
            if (removed == 0) {
                error = TransactionError.Deficient(0)
            }
            replace(itemSlot, "basket", "${veg.plural}_$removed")
        }
        when (player.inventory.transaction.error) {
            is TransactionError.Deficient -> player.message("You don't have any fruit with which to fill the basket.")
            TransactionError.None -> {}
            else -> logger.warn { "Error filling fruit basket." }
        }
    }

    @Inventory("Fill", "strawberries_*")
    @Inventory("Fill", "oranges_*")
    @Inventory("Fill", "bananas_*")
    @Inventory("Fill", "apples_*")
    @Inventory("Fill", "tomatoes_*")
    fun fill(player: Player, item: Item, itemSlot: Int) {
        val fruit = fruit[item.id] ?: return
        fill(player, item, itemSlot, fruit)
    }

    private fun fill(player: Player, item: Item, itemSlot: Int, fruit: Fruit) {
        val current = item.id.removePrefix("${fruit.plural}_").toInt()
        player.inventory.transaction {
            val removed = removeToLimit(item.id, 5 - current)
            if (removed == 0) {
                error = TransactionError.Deficient(0)
            }
            replace(itemSlot, item.id, "${fruit.plural}_${current + removed}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Deficient -> {}
            TransactionError.None -> {}
            else -> logger.warn { "Error filling ${fruit.plural}." }
        }
    }

    @Inventory("Fill", "strawberries_5")
    @Inventory("Fill", "oranges_5")
    @Inventory("Fill", "bananas_5")
    @Inventory("Fill", "apples_5")
    @Inventory("Fill", "tomatoes_5")
    fun full(player: Player, item: Item) {
        val fruit = fruit[item.id] ?: return
        player.message("The ${fruit.name} basket is already full.")
    }

    @UseOn("strawberry", "strawberries_5")
    @UseOn("orange", "oranges_5")
    @UseOn("banana", "bananas_5")
    @UseOn("cooking_apple", "apples_5")
    @UseOn("tomato", "tomatoes_5")
    fun full(player: Player, fromItem: Item, toItem: Item) {
        val fruit = fruit[fromItem.id] ?: return
        player.message("The ${fruit.name} basket is already full.")
    }

    @UseOn("strawberry", "strawberries_*")
    @UseOn("orange", "oranges_*")
    @UseOn("banana", "bananas_*")
    @UseOn("cooking_apple", "apples_*")
    @UseOn("tomato", "tomatoes_*")
    fun fill(player: Player, fromItem: Item, fromSlot: Int, toItem: Item) {
        val fruit = fruit[fromItem.id] ?: return
        fill(player, fromItem, fromSlot, fruit)
    }

    @Inventory("Remove-one", "strawberries_*")
    @Inventory("Remove-one", "oranges_*")
    @Inventory("Remove-one", "bananas_*")
    @Inventory("Remove-one", "apples_*")
    @Inventory("Remove-one", "tomatoes_*")
    fun removeOne(player: Player, item: Item, itemSlot: Int) {
        val fruit = fruit.values.firstOrNull { item.id.startsWith(it.plural) } ?: return
        val current = item.id.removePrefix("${fruit.plural}_").toInt()

        player.inventory.transaction {
            add(fruit.id)
            replace(itemSlot, item.id, if (current == 1) "basket" else "${fruit.plural}_${current - 1}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> player.message("You take ${fruit.description} out of the ${fruit.name} basket.")
            else -> logger.warn { "Error emptying ${fruit.description}." }
        }
    }

    @Inventory("Empty", "strawberries_*")
    @Inventory("Empty", "oranges_*")
    @Inventory("Empty", "bananas_*")
    @Inventory("Empty", "apples_*")
    @Inventory("Empty", "tomatoes_*")
    fun empty(player: Player, item: Item, itemSlot: Int) {
        val fruit = fruit.values.firstOrNull { item.id.startsWith(it.plural) } ?: return
        val current = item.id.removePrefix("${fruit.plural}_").toInt()

        player.inventory.transaction {
            val added = addToLimit(fruit.id, current)
            if (added == 0) {
                error = TransactionError.Full(0)
            }
            replace(itemSlot, item.id, if (added == current) "basket" else "${fruit.plural}_${current - added}")
        }

        when (player.inventory.transaction.error) {
            is TransactionError.Full -> player.inventoryFull()
            TransactionError.None -> {}
            else -> logger.warn { "Error emptying ${fruit.plural}." }
        }
    }
}

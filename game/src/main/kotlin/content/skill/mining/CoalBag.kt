package content.skill.mining

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.addToLimit
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.removeToLimit
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.Subscribe
import world.gregs.voidps.type.sub.UseOn

class CoalBag {

    private val bagCapacity = 81

    @Inventory("Inspect", "coal_bag")
    fun inspect(player: Player) {
        val coal = player["coal_bag_coal", 0]
        if (coal == 0) {
            player.message("Your coal bag is empty.")
        } else {
            player.message("Your coal bag has $coal ${"piece".plural(coal)} of coal in it.")
        }
    }

    @Inventory("Withdraw-one", "coal_bag")
    fun withdrawOne(player: Player) {
        val coal = player["coal_bag_coal", 0]
        if (coal == 0) {
            player.message("There is no coal in your bag to withdraw.")
            return
        }
        if (player.inventory.add("coal")) {
            player["coal_bag_coal"] = (coal - 1).coerceAtLeast(0)
        } else {
            player.inventoryFull()
        }
    }

    @Inventory("Withdraw-many", "coal_bag")
    fun withdrawMany(player: Player) {
        val count = player["coal_bag_coal", 0]
        if (count == 0) {
            player.message("There is no coal in your bag to withdraw.")
            return
        }
        val added = player.inventory.addToLimit("coal", count)
        if (added == 0) {
            return
        }
        player["coal_bag_coal"] = (count - added).coerceAtLeast(0)
        player.message("You withdraw some coal.")
    }

    @UseOn("coal", "coal_bag")
    fun fill(player: Player, fromItem: Item, toItem: Item) {
        val coal = player["coal_bag_coal", 0]
        if (coal == bagCapacity) {
            player.message("The coal bag is already full.")
            return
        }
        val limit = bagCapacity - coal
        val removed = player.inventory.removeToLimit("coal", limit)
        if (removed == 0) {
            return
        }
        player["coal_bag_coal"] = (coal + removed).coerceAtMost(bagCapacity)
        player.message("You add the coal to your bag.")
    }

    @Subscribe("can_destroy", "coal_bag")
    fun destroy(player: Player): Boolean {
        val coal = player["coal_bag_coal", 0]
        if (coal > 0) {
            player.message("You can't destroy this item with coal in it.")
            return true
        }
        return false
    }

}

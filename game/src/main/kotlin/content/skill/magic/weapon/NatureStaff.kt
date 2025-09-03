package content.skill.magic.weapon

import content.entity.player.inv.inventoryItem
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.interact.itemOnItem
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.charge
import world.gregs.voidps.engine.inv.transact.discharge
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.UseOn

class NatureStaff {

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("nature_staff_charges")
    }

    @Inventory("Inspect", "nature_staff")
    fun inspect(player: Player, item: Item, itemSlot: Int) {
        val charges = player.inventory.charges(player, itemSlot)
        player.message("The staff has ${if (charges == 0) "no" else charges} ${"charge".plural(charges)}.")
    }

    @Inventory("Empty", "nature_staff")
    fun empty(player: Player, item: Item, itemSlot: Int) {
        val charges = player.inventory.charges(player, itemSlot)
        if (charges == 0) {
            player.message("The staff has no charges for your to remove.")
            return
        }
        val success = player.inventory.transaction {
            val added = addToLimit("nature_rune", charges)
            if (added <= 0) {
                error = TransactionError.Deficient(charges)
            } else {
                discharge(player, itemSlot, amount = added)
            }
        }
        if (success) {
            player.message("You remove charges from the staff and retrieve some nature runes.")
        } else {
            player.inventoryFull()
        }
    }

    @UseOn("nature_rune", "nature_staff")
    fun fill(player: Player, fromItem: Item, toItem: Item, toSlot: Int) {
        val maximum = toItem.def.getOrNull<Int>("charges_max") ?: return
        val spaces = maximum - player.inventory.charges(player, toSlot)
        val count = player.inventory.count(fromItem.id).coerceAtMost(spaces)
        if (count <= 0) {
            player.message("The staff already has the maximum amount of charges.")
            return
        }
        val success = player.inventory.transaction {
            remove(fromItem.id, count)
            charge(player, toSlot, count)
        }
        if (success) {
            player.message("You charge the staff with nature runes.")
        }
    }

}

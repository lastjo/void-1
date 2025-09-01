package content.quest.member.tower_of_life

import content.entity.player.dialogue.type.item
import content.entity.player.inv.inventoryItem
import net.pearx.kasechange.toLowerSpaceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnItem
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.engine.inv.transact.operation.SetCharge.setCharge
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class Satchel {

    private val cake = 0x1
    private val banana = 0x2
    private val sandwich = 0x4

    @Inventory("Inspect", "*_satchel")
    suspend fun inspect(player: Player, item: Item, itemSlot: Int) = player.dialogue {
        val charges = player.inventory.charges(player, itemSlot)
        val cake = if (charges and cake != 0) "one" else "no"
        val banana = if (charges and banana != 0) "one" else "no"
        val sandwich = if (charges and sandwich != 0) "one" else "no"
        item(item.id, 400, "The ${item.id.toLowerSpaceCase()}!<br>(Containing: $sandwich sandwich, $cake cake, and $banana banana)")
    }

    @Inventory("Empty", "*_satchel")
    fun empty(player: Player, itemSlot: Int) {
        var charges = player.inventory.charges(player, itemSlot)
        charges = withdraw(player, itemSlot, charges, "banana", banana)
        charges = withdraw(player, itemSlot, charges, "cake", cake)
        withdraw(player, itemSlot, charges, "triangle_sandwich", sandwich)
    }

    @UseOn("*_satchel", "cake")
    @UseOn("cake", "*_satchel")
    fun cake(player: Player, fromItem: Item, fromSlot: Int, toSlot: Int) {
        val charges = player.inventory.charges(player, toSlot)
        if (charges and cake != 0) {
            player.message("You already have a cake in there.")
            return
        }
        player.inventory.transaction {
            remove(fromSlot, "cake")
            setCharge(toSlot, charges + cake)
        }
    }

    @UseOn("*_satchel", "banana")
    @UseOn("banana", "*_satchel")
    fun banana(player: Player, fromItem: Item, fromSlot: Int, toSlot: Int) {
        val charges = player.inventory.charges(player, toSlot)
        if (charges and banana != 0) {
            player.message("You already have a banana in there.")
            return
        }
        player.inventory.transaction {
            remove(fromSlot, "banana")
            setCharge(toSlot, charges + banana)
        }
    }

    @UseOn("*_satchel", "triangle_sandwich")
    @UseOn("triangle_sandwich", "*_satchel")
    fun sandwich(player: Player, fromItem: Item, fromSlot: Int, toSlot: Int) {
        val charges = player.inventory.charges(player, toSlot)
        if (charges and sandwich != 0) {
            player.message("You already have a sandwich in there.")
            return
        }
        player.inventory.transaction {
            remove(fromSlot, "triangle_sandwich")
            setCharge(toSlot, charges + sandwich)
        }
    }

    fun withdraw(player: Player, slot: Int, charges: Int, id: String, food: Int): Int {
        if (charges and food != 0) {
            val success = player.inventory.transaction {
                add(id)
                setCharge(slot, charges and food.inv())
            }
            if (success) {
                return charges and food.inv()
            } else {
                player.message("You don't have enough free space to empty your satchel.")
            }
        }
        return charges
    }
}

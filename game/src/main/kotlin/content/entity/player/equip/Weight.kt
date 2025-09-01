package content.entity.player.equip

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.network.login.protocol.encode.weight
import world.gregs.voidps.type.sub.InventoryUpdated
import world.gregs.voidps.type.sub.Spawn

class Weight {

    @InventoryUpdated
    @InventoryUpdated("worn_equipment")
    fun updateWeight(player: Player) {
        var weight = 0.0
        weight += player.equipment.weight()
        weight += player.inventory.weight()

        player["weight"] = weight
        player.client?.weight(weight.toInt())
    }

    @Spawn
    fun spawn(player: Player) {
        updateWeight(player)
    }

    fun Inventory.weight(): Double = items.sumOf { it.def["weight", 0.0] * it.amount }

}

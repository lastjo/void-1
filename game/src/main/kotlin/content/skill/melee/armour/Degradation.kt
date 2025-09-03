package content.skill.melee.armour

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.*
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.InventorySlotChanged

@Script
class Degradation {

    val slots = arrayOf(
        EquipSlot.Hat.index,
        EquipSlot.Weapon.index,
        EquipSlot.Chest.index,
        EquipSlot.Shield.index,
        EquipSlot.Legs.index,
    )

    @Combat(stage = CombatStage.DAMAGE)
    fun damage(player: Player, target: Character) {
        degrade(player)
    }

    @Combat
    fun attack(player: Player, target: Character) {
        degrade(player)
    }

    @InventorySlotChanged("worn_equipment")
    fun change(player: Player, inventory: String, item: Item, fromItem: Item, fromSlot: Int) {
        val degrade: String = fromItem.def.getOrNull("degrade") ?: return
        if (degrade == "destroy" && item.isNotEmpty()) {
            return
        }
        if (item.id != degrade) {
            return
        }
        if (player.inventories.inventory(inventory).charges(player, fromSlot) != 0) {
            return
        }
        val message: String = fromItem.def.getOrNull("degrade_message") ?: return
        player.message(message)
    }

    fun degrade(player: Player) {
        if (player.hasClock("degraded")) {
            return
        }
        player.start("degraded", 1)
        val inventory = player.equipment
        for (slot in slots) {
            val equipment = inventory.getOrNull(slot) ?: continue
            val deplete: String = equipment.def.getOrNull("deplete") ?: continue
            if (deplete != "combat") {
                continue
            }
            inventory.discharge(player, slot)
        }
    }
}

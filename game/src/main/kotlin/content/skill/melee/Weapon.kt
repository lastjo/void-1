package content.skill.melee

import content.skill.melee.weapon.attackRange
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.InventorySlotChanged
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.Variable

class Weapon {

    @Spawn
    fun spawn(player: Player) {
        updateWeapon(player, player.equipped(EquipSlot.Weapon))
    }

    @InventorySlotChanged("worn_equipment", EquipSlot.WEAPON)
    fun change(player: Player, item: Item) {
        updateWeapon(player, item)
    }

    @Variable("autocast", toNull = true)
    @Variable("spell", toNull = true)
    @Variable("attack_style", to = "long_range")
    @Variable("attack_style", from = "long_range")
    fun update(player: Player) {
        updateWeapon(player, player.weapon)
    }

    fun updateWeapon(player: Player, weapon: Item, range: Int = 0) {
        player.attackRange = if (player.contains("autocast")) 8 else (weapon.def["attack_range", 1] + range).coerceAtMost(10)
        player["attack_speed"] = weapon.def["attack_speed", 4]
        player.weapon = weapon
    }
}

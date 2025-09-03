package content.skill.melee.armour

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inv.itemAdded
import world.gregs.voidps.engine.inv.itemRemoved
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class VoidSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet("")) {
            player["void_set_effect"] = true
        } else if (player.hasFullSet("elite_")) {
            player["elite_void_set_effect"] = true
        }
    }

    @ItemAdded("void_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.HANDS], inventory = "worn_equipment")
    @ItemAdded("elite_void_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.HANDS], inventory = "worn_equipment")
    fun equip(player: Player) {
        if (player.hasFullSet("")) {
            player["void_set_effect"] = true
        } else if (player.hasFullSet("elite_")) {
            player["elite_void_set_effect"] = true
        }
    }

    @ItemRemoved("void_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.HANDS], inventory = "worn_equipment")
    @ItemRemoved("elite_void_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.HANDS], inventory = "worn_equipment")
    fun unequip(player: Player) {
        player.clear("void_set_effect")
        player.clear("elite_void_set_effect")
    }

    fun Player.hasFullSet(prefix: String): Boolean = equipped(EquipSlot.Chest).id.startsWith("${prefix}void_knight_top") &&
        equipped(EquipSlot.Legs).id.startsWith("${prefix}void_knight_robe") &&
        equipped(EquipSlot.Hands).id.startsWith("void_knight_gloves") &&
        isHelm(equipped(EquipSlot.Hat))

    fun isHelm(item: Item): Boolean = when (item.id) {
        "void_ranger_helm", "void_melee_helm", "void_mage_helm" -> true
        else -> false
    }
}

package content.skill.melee.armour.barrows

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class DharoksSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet()) {
            player["dharoks_set_effect"] = true
        }
    }

    @ItemAdded("dharoks_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.hasFullSet()) {
            player["dharoks_set_effect"] = true
        }
    }

    @ItemRemoved("dharoks_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun remove(player: Player) {
        player.clear("dharoks_set_effect")
    }

    fun Player.hasFullSet() = BarrowsArmour.hasSet(
        this,
        "dharoks_greataxe",
        "dharoks_helm",
        "dharoks_platebody",
        "dharoks_platelegs",
    )
}

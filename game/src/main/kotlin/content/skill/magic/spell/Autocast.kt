package content.skill.magic.spell

import content.skill.melee.weapon.attackRange
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.*

class Autocast(private val interfaceDefinitions: InterfaceDefinitions) {

    @Interface("Autocast", id = "*_spellbook")
    fun toggle(player: Player, id: String, component: String) {
        val value: Int? = interfaceDefinitions.getComponent(id, component)?.getOrNull("cast_id")
        if (value == null || player["autocast", 0] == value) {
            player.clear("autocast")
        } else {
            player["autocast_spell"] = component
            player.attackRange = 8
            player["autocast"] = value
        }
    }

    @Variable("autocast", toNull = true)
    fun clear(player: Player) {
        player.clear("autocast_spell")
    }

    @InventorySlotChanged("worn_equipment", slot = EquipSlot.WEAPON)
    fun update(player: Player) {
        player.clear("autocast")
    }
}

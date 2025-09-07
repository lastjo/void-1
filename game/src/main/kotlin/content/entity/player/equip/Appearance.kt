package content.entity.player.equip

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.flagAppearance
import world.gregs.voidps.network.login.protocol.visual.update.player.Body
import world.gregs.voidps.network.login.protocol.visual.update.player.BodyPart
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.InventorySlotChanged

class Appearance {

    @InventorySlotChanged("worn_equipment")
    fun update(player: Player, itemSlot: Int) {
        if (needsUpdate(itemSlot, player.body)) {
            player.flagAppearance()
        }
    }

    fun needsUpdate(index: Int, parts: Body): Boolean {
        val slot = EquipSlot.by(index)
        val part = BodyPart.by(slot) ?: return false
        return parts.updateConnected(part)
    }
}

package content.skill.magic.jewellery

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class RingOfWealth {

    @Spawn
    fun spawn(player: Player) {
        player["wearing_ring_of_wealth"] = player.equipped(EquipSlot.Ring).id == "ring_of_wealth"
    }

    @ItemAdded("ring_of_wealth", slots = [EquipSlot.RING], inventory = "worn_equipment")
    fun added(player: Player) {
        player["wearing_ring_of_wealth"] = true
    }

    @ItemRemoved("ring_of_wealth", slots = [EquipSlot.RING], inventory = "worn_equipment")
    fun removed(player: Player) {
        player["wearing_ring_of_wealth"] = false
    }
}

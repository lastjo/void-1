package content.skill.melee.weapon

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class Whacking {

    @Spawn
    fun spawn(player: Player) {
        if (player.weapon.id == "rubber_chicken" || player.weapon.id == "easter_carrot") {
            player.options.set(5, "Whack")
        }
    }

    @ItemAdded("rubber_chicken", "easter_carrot", slots = [EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        player.options.set(5, "Whack")
    }

    @ItemRemoved("rubber_chicken", "easter_carrot", slots = [EquipSlot.WEAPON], inventory = "worn_equipment")
    fun removed(player: Player) {
        player.options.remove("Whack")
    }
}

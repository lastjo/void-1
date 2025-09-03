package content.skill.melee.armour

import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.mode.move.enterArea
import world.gregs.voidps.engine.entity.character.mode.move.exitArea
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.itemAdded
import world.gregs.voidps.engine.inv.itemRemoved
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Enter
import world.gregs.voidps.type.sub.Exit
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved

class CastleWarsBrace(areas: AreaDefinitions) {

    val area = areas["castle_wars"]

    @Enter("castle_wars")
    fun enter(player: Player) {
        if (player.equipped(EquipSlot.Hands).id.startsWith("castle_wars_brace")) {
            player["castle_wars_brace"] = true
        }
    }

    @Exit("castle_wars")
    fun exit(player: Player) {
        if (player.equipped(EquipSlot.Hands).id.startsWith("castle_wars_brace")) {
            player.clear("castle_wars_brace")
        }
    }

    // TODO should be activated on game start not equip.
    @ItemAdded("castle_wars_brace*", slots = [EquipSlot.HANDS], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.tile in area) {
            player["castle_wars_brace"] = true
        }
    }

    @ItemRemoved("castle_wars_brace*", slots = [EquipSlot.HANDS], inventory = "worn_equipment")
    fun removed(player: Player) {
        if (player.tile in area) {
            player.clear("castle_wars_brace")
        }
    }

}

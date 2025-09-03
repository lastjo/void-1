package content.area.misthalin.barbarian_village.stronghold_of_security

import com.github.michaelbull.logging.InlineLogger
import content.skill.magic.spell.Teleport
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.discharge
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Inventory

class SkullSceptre {

    val logger = InlineLogger()

    @Inventory("Invoke", "skull_sceptre")
    fun teleport(player: Player) {
        Teleport.teleport(player, Tile(3081, 3421), "skull_sceptre")
    }

    @world.gregs.voidps.type.sub.Teleport("skull_sceptre")
    fun tele(player: Player): Int {
        if (player.equipped(EquipSlot.Weapon).id == "skull_sceptre") {
            if (!player.equipment.discharge(player, EquipSlot.Weapon.index, 1)) {
                logger.warn { "Failed to discharge skull sceptre for $player" }
                return -1
            }
            return 0
        }
        val index = player.inventory.indexOf("skull_sceptre")
        if (index == -1) {
            logger.warn { "Failed to find skull sceptre for $player" }
            return -1
        }
        if (!player.inventory.discharge(player, index, 1)) {
            logger.warn { "Failed to discharge skull sceptre for $player" }
            return -1
        }
        return 0
    }

    @Inventory("Divine", "skull_sceptre")
    fun check(player: Player, item: Item) {
        val charges = item.charges()
        // TODO proper message
        player.message("The sceptre has $charges ${"charge".plural(charges)} remaining.")
    }
}

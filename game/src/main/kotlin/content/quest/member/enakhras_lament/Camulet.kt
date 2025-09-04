package content.quest.member.enakhras_lament

import content.entity.player.dialogue.type.statement
import content.skill.magic.jewellery.jewelleryTeleport
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class Camulet(private val areas: AreaDefinitions) {

    @Inventory("Rub", "camulet")
    suspend fun rub(player: Player, inventory: String, itemSlot: Int) {
        if (jewelleryTeleport(player, inventory, itemSlot, areas["camulet_teleport"])) {
            player.message("You rub the amulet...")
        } else {
            player.dialogue {
                statement("Your Camulet has run out of teleport charges. You can renew them by applying camel dung.")
            }
        }
    }

    @Inventory("Check-charge", "camulet", "inventory")
    fun check(player: Player, itemSlot: Int) {
        val charges = player.inventory.charges(player, itemSlot)
        player.message("Your Camulet has $charges ${"charge".plural(charges)} left.")
        if (charges == 0) {
            player.message("You can recharge it by applying camel dung.")
        }
    }

    @UseOn("ugthanki_dung", "camulet")
    fun use(player: Player, fromItem: Item, toSlot: Int) {
        val charges = player.inventory.charges(player, toSlot)
        if (charges == 4) {
            player.message("Your Camulet already has 4 charges.")
            return
        }
        if (player.inventory.replace("ugthanki_dung", "bucket")) {
            player.message("You recharge the Camulet using camel dung. Yuck!")
            player["camulet_charges"] = 4
        }
    }
}

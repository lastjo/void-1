package content.skill.magic.jewellery

import content.entity.player.dialogue.type.choice
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Inventory

class AmuletOfGlory(
    areas: AreaDefinitions,
) {

    val edgeville = areas["edgeville_teleport"]
    val karamja = areas["karamja_teleport"]
    val draynorVillage = areas["draynor_village_teleport"]
    val alKharid = areas["al_kharid_teleport"]

    @Inventory("Rub", "amulet_of_glory_#")
    suspend fun rub(player: Player, item: Item, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Where would you like to teleport to?") {
            option("Edgeville") {
                player.message("You rub the amulet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, edgeville)
            }
            option("Karamja") {
                player.message("You rub the amulet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, karamja)
            }
            option("Draynor Village") {
                player.message("You rub the amulet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, draynorVillage)
            }
            option("Al Kharid") {
                player.message("You rub the amulet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, alKharid)
            }
            option("Nowhere")
        }
    }

    @Inventory(item = "amulet_of_glory_#", inventory = "worn_equipment")
    fun tele(player: Player, item: Item, option: String, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        val area = when (option) {
            "Edgeville" -> edgeville
            "Karamja" -> karamja
            "Draynor Village" -> draynorVillage
            "Al Kharid" -> alKharid
            else -> return
        }
        player.message("You rub the amulet...", ChatType.Filter)
        jewelleryTeleport(player, inventory, itemSlot, area)
    }
}

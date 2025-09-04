package content.minigame.pyramid_plunder

import content.entity.player.dialogue.type.choice
import content.skill.magic.jewellery.itemTeleport
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Inventory

class PharaohsSceptre(
    areas: AreaDefinitions,
) {

    val jalsavrah = areas["jalsavrah_teleport"]
    val jaleustrophos = areas["jaleustrophos_teleport"]
    val jaldraocht = areas["jaldraocht_teleport"]

    @Inventory("Teleport", "pharaohs_sceptre_*")
    suspend fun teleport(player: Player, itemSlot: Int, inventory: String) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Which Pyramid do you want to teleport to?") {
            option("Jalsavrah") {
                itemTeleport(player, inventory, itemSlot, jalsavrah, "pharaohs_sceptre")
            }
            option("Jaleustrophos") {
                itemTeleport(player, inventory, itemSlot, jaleustrophos, "pharaohs_sceptre")
            }
            option("Jaldraocht") {
                itemTeleport(player, inventory, itemSlot, jaldraocht, "pharaohs_sceptre")
            }
            option("I'm happy where I am actually.")
        }
    }

    @Inventory(item = "pharaohs_sceptre_*", inventory = "worn_equipment")
    suspend fun sceptre(player: Player, option: String, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        val area = when (option) {
            "Jalsavrah" -> jalsavrah
            "Jaleustrophos" -> jaleustrophos
            "Jaldraocht" -> jaldraocht
            else -> return@dialogue
        }
        itemTeleport(player, inventory, itemSlot, area, "pharaohs_sceptre")
    }
}

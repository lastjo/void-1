package content.skill.magic.jewellery

import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.statement
import content.quest.questCompleted
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Inventory

class GamesNecklace(areas: AreaDefinitions) {

    val burthorpe = areas["burthorpe_teleport"]
    val barbarianOutput = areas["barbarian_outpost_teleport"]
    val clanWars = areas["clan_wars_teleport"]
    val wildernessVolcano = areas["wilderness_volcano_teleport"]
    val burghDeRott = areas["burgh_de_rott_teleport"]

    @Inventory("Rub", "games_necklace_*")
    suspend fun rub(player: Player, item: Item, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Where would you like to teleport to?") {
            option("Burthorpe Games Rooms.") {
                jewelleryTeleport(player, inventory, itemSlot, burthorpe)
            }
            option("Barbarian Outpost.") {
                jewelleryTeleport(player, inventory, itemSlot, barbarianOutput)
            }
            option("Clan Wars.") {
                jewelleryTeleport(player, inventory, itemSlot, clanWars)
            }
            option("Wilderness Volcano.") {
                jewelleryTeleport(player, inventory, itemSlot, wildernessVolcano)
            }
            option("Burgh De Rott.", { player.questCompleted("darkness_of_hallowvale") }) {
                jewelleryTeleport(player, inventory, itemSlot, burghDeRott)
            }
            option("Nowhere.", { !player.questCompleted("darkness_of_hallowvale") })
        }
    }

    @Inventory(item = "games_necklace_*", inventory = "worn_equipment")
    suspend fun tele(player: Player, item: Item, option: String, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        val area = when (option) {
            "Burthorpe" -> burthorpe
            "Barbarian Outpost" -> barbarianOutput
            "Clan Wars" -> clanWars
            "Wilderness Volcano" -> wildernessVolcano
            "Burgh De Rott" -> {
                if (!player.questCompleted("darkness_of_hallowvale")) {
                    player.dialogue {
                        statement("You need to have completed The Darkness of Hallowvale quest to teleport to this location.")
                    }
                    return
                }
                burghDeRott
            }
            else -> return
        }
        jewelleryTeleport(player, inventory, itemSlot, area)
    }
}

package content.skill.magic.jewellery

import content.entity.player.dialogue.type.choice
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Inventory

class CombatBracelet(areas: AreaDefinitions) {

    val warriorsGuild = areas["warriors_guild_teleport"]
    val championsGuild = areas["champions_guild_teleport"]
    val monastery = areas["monastery_teleport"]
    val rangingGuild = areas["ranging_guild_teleport"]

    @Inventory("Rub", "combat_bracelet_*")
    suspend fun rub(player: Player, item: Item, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Where would you like to teleport to?") {
            option("Warriors' Guild") {
                player.message("You rub the bracelet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, warriorsGuild)
            }
            option("Champions' Guild") {
                player.message("You rub the bracelet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, championsGuild)
            }
            option("Monastery") {
                player.message("You rub the bracelet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, monastery)
            }
            option("Ranging Guild") {
                player.message("You rub the bracelet...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, rangingGuild)
            }
            option("Nowhere")
        }
    }

    @Inventory(item = "combat_bracelet_*", inventory = "worn_equipment")
    fun tele(player: Player, item: Item, option: String, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        val area = when (option) {
            "Warriors' Guild" -> warriorsGuild
            "Champions' Guild" -> championsGuild
            "Monastery" -> monastery
            "Ranging Guild" -> rangingGuild
            else -> return
        }
        player.message("You rub the bracelet...", ChatType.Filter)
        jewelleryTeleport(player, inventory, itemSlot, area)
    }
}

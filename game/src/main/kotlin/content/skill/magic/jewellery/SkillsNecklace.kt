package content.skill.magic.jewellery

import content.entity.player.dialogue.type.choice
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Inventory

class SkillsNecklace(areas: AreaDefinitions) {

    val fishing = areas["fishing_guild_teleport"]
    val mining = areas["mining_guild_teleport"]
    val crafting = areas["crafting_guild_teleport"]
    val cooking = areas["cooking_guild_teleport"]

    @Inventory("Rub", "skills_necklace_*")
    suspend fun rub(player: Player, item: Item, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Where would you like to teleport to?") {
            option("Fishing Guild.") {
                player.message("You rub the necklace...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, fishing)
            }
            option("Mining Guild.") {
                player.message("You rub the necklace...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, mining)
            }
            option("Crafting Guild.") {
                player.message("You rub the necklace...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, crafting)
            }
            option("Cooking Guild.") {
                player.message("You rub the necklace...", ChatType.Filter)
                jewelleryTeleport(player, inventory, itemSlot, cooking)
            }
            option("Nowhere.")
        }
    }

    @Inventory(item = "skills_necklace_#", inventory = "worn_equipment")
    fun tele(player: Player, item: Item, option: String, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        val area = when (option) {
            "Fishing Guild" -> fishing
            "Mining Guild" -> mining
            "Crafting Guild" -> crafting
            "Cooking Guild" -> cooking
            else -> return
        }
        player.message("You rub the necklace...", ChatType.Filter)
        jewelleryTeleport(player, inventory, itemSlot, area)
    }
}

package content.area.misthalin.lumbridge.church

import content.quest.questCompleted
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.notEnough
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class GravestoneShop(private val enums: EnumDefinitions) {

    @Open("gravestone_shop")
    fun open(player: Player) {
        player.sendVariable("gravestone_current")
        if (player.questCompleted("the_restless_ghost")) {
            player.addVarbit("unlocked_gravestones", "flag")
            player.addVarbit("unlocked_gravestones", "small_gravestone")
            player.addVarbit("unlocked_gravestones", "ornate_gravestone")
        }
        if (player.questCompleted("the_giant_dwarf")) {
            player.addVarbit("unlocked_gravestones", "font_of_life")
            player.addVarbit("unlocked_gravestones", "stele")
            player.addVarbit("unlocked_gravestones", "symbol_of_saradomin")
            player.addVarbit("unlocked_gravestones", "symbol_of_zamorak")
            player.addVarbit("unlocked_gravestones", "symbol_of_guthix")
            player.addVarbit("unlocked_gravestones", "angel_of_death")
            if (player.questCompleted("land_of_the_goblins")) {
                player.addVarbit("unlocked_gravestones", "symbol_of_bandos")
            }
            if (player.questCompleted("temple_of_ikov")) {
                player.addVarbit("unlocked_gravestones", "symbol_of_armadyl")
            }
            if (player.questCompleted("desert_treasure")) {
                player.addVarbit("unlocked_gravestones", "ancient_symbol")
            }
        }
        if (player.questCompleted("king_of_the_dwarves")) {
            player.addVarbit("unlocked_gravestones", "royal_dwarven_gravestone")
        }
        player.interfaceOptions.unlockAll("gravestone_shop", "button", 0 until 13)
    }

    @Interface(component = "button", id = "gravestone_shop")
    fun select(player: Player, itemSlot: Int) {
        val name = enums.get("gravestone_names").getString(itemSlot)
        val id = name.replace(" ", "_").lowercase()
        if (player["gravestone_current", "memorial_plaque"] == id) {
            return
        }
        val cost = enums.get("gravestone_price").getInt(itemSlot)
        if (cost > 0 && !player.inventory.remove("coins", cost)) {
            player.notEnough("coins")
            return
        }
        player["gravestone_current"] = id
    }
}

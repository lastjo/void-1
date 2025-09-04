package content.quest.member.mahjarrat.the_dig_site

import content.skill.magic.jewellery.jewelleryTeleport
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.type.sub.Inventory

class DigSitePendant(areas: AreaDefinitions) {

    private val digSite = areas["dig_site_teleport"]

    @Inventory("Rub", "dig_site_pendant_#")
    fun rub(player: Player, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        player.message("You rub the pendant...", ChatType.Filter)
        jewelleryTeleport(player, inventory, itemSlot, digSite)
    }
}

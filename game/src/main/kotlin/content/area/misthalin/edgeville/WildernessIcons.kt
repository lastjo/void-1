package content.area.misthalin.edgeville

import content.area.wilderness.inWilderness
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.PrayerStart
import world.gregs.voidps.type.sub.PrayerStop
import world.gregs.voidps.type.sub.Variable

class WildernessIcons {

    @Variable("in_wilderness", toBool = "true")
    fun set(player: Player) {
        player.options.set(1, "Attack")
        player.open("wilderness_skull")
        //    player.setVar("no_pvp_zone", false)
        resetIcons(player)
        updateIcon(player)
    }

    @Variable("in_wilderness", toNull = true)
    fun clear(player: Player) {
        player.options.remove("Attack")
        player.close("wilderness_skull")
        //    player.setVar("no_pvp_zone", true)
        resetIcons(player)
    }

    @Open("wilderness_skull")
    fun open(player: Player, id: String) {
        player.interfaces.sendSprite(id, "right_skull", 439)
    }

    @PrayerStart("protect_item")
    fun pray(player: Player, id: String) {
        if (player.inWilderness) {
            resetIcons(player)
            updateIcon(player)
        }
    }

    @PrayerStop("protect_item")
    fun stop(player: Player, id: String) {
        if (player.inWilderness) {
            resetIcons(player)
            updateIcon(player)
        }
    }

    fun resetIcons(player: Player) = player.interfaces.apply {
        sendVisibility("area_status_icon", "protect_disabled", false)
        sendVisibility("area_status_icon", "no_protection", false)
        sendVisibility("area_status_icon", "protection_active", false)
    }

    fun updateIcon(player: Player) {
        //    val component = when {
        //        player["prayer_protect_item", false] -> "protection_active"
        //        player.has(Skill.Prayer, if (player.isCurses()) 50 else 25) -> "protect_disabled"
        //        else -> "no_protection"
        //    }
        // These icons aren't displayed in this revision.
        //    player.interfaces.sendVisibility("area_status_icon", component, true)
    }
}

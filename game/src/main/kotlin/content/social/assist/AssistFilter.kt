package content.social.assist

import content.social.assist.Assistance.getHoursRemaining
import content.social.assist.Assistance.hasEarnedMaximumExperience
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.type.sub.Interface

class AssistFilter {

    @Interface("XP Earned/Time", "assist", "filter_buttons")
    fun check(player: Player) {
        if (hasEarnedMaximumExperience(player)) {
            val hours = getHoursRemaining(player)
            player.message(
                "You've earned the maximum XP (30,000 Xp) from the Assist System within a 24-hour period.",
                ChatType.Assist,
            )
            player.message("You can assist again in $hours ${"hour".plural(hours)}.", ChatType.Assist)
        } else {
            val earned = player["total_xp_earned", 0.0]
            player.message("You have earned $earned Xp. The Assist system is available to you.", ChatType.Assist)
        }
    }

    @Interface("On Assist", "assist", "filter_buttons")
    fun on(player: Player) {
        player["assist_status"] = "on"
    }

    @Interface("Friends Assist", "assist", "filter_buttons")
    fun friends(player: Player) {
        player["assist_status"] = "friends"
        cancel(player)
    }

    @Interface("Off Assist", "assist", "filter_buttons")
    fun off(player: Player) {
        player["assist_status"] = "off"
        cancel(player)
    }

    /**
     * Assistance privacy filter settings
     */

    fun cancel(player: Player) {
        if (player.contains("assistant")) {
            val assistant: Player? = player["assistant"]
            assistant?.closeMenu()
        }

        if (player.contains("assisted")) {
            player.closeMenu()
        }
    }
}

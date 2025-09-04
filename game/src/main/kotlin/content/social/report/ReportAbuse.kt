package content.social.report

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.hasMenuOpen
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface

class ReportAbuse {

    @Interface("Report Abuse", "report", "filter_buttons")
    fun report(player: Player) {
        if (player.hasMenuOpen()) {
            player.message("Please finish what you're doing first.")
            return
        }
        player.open("report_abuse_select")
    }
}

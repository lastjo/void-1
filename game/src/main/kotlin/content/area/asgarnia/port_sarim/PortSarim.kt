package content.area.asgarnia.port_sarim

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.holdsItem
import world.gregs.voidps.type.sub.Take

class PortSarim {

    @Take("white_apron_port_sarim")
    fun canTake(player: Player): String {
        if (player.holdsItem("white_apron")) {
            player.message("You already have one of those.")
            return "null"
        }
        player.anim("take")
        player.message("You take an apron. It feels freshly starched and smells of laundry.")
        return "white_apron"
    }

}

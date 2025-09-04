package content.entity.player.dialogue.type

import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Context
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface

suspend fun Context<Player>.warning(id: String): Boolean {
    val count = player["warning_$id", 0]
    if (count == 7) {
        return true
    }
    check(player.open("warning_$id")) { "Unable to open warning dialogue warning_$id for $player" }
    player.interfaces.sendVisibility("warning_$id", "ask_again", count == 6)
    val result = StringSuspension.get(player) == "yes"
    player.close("warning_$id")
    return result
}

class Warning {

    @Interface("Yes", "yes", "warning_*")
    @Interface("No", "no", "warning_*")
    fun select(player: Player, component: String) {
        (player.dialogueSuspension as StringSuspension).resume(component)
    }

    @Interface("Off/On", "dont_ask", "warning_*")
    fun toggle(player: Player, id: String) {
        val count = player[id, 0]
        if (count == 6) {
            player[id] = 7
        } else {
            player[id] = 6
        }
    }

}

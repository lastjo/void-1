package content.area.asgarnia.falador

import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.flagAppearance
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

internal suspend fun Dialogue.openDressingRoom(id: String) {
    player.closeDialogue()
    player.delay(1)
    player.gfx("dressing_room_start")
    player.delay(1)
    player.open(id)
    player.softTimers.start("dressing_room")
}

class DressingRoom {

    @TimerStart("dressing_room")
    fun start(player: Player): Int = 1

    @TimerTick("dressing_room")
    fun tick(player: Player): Int {
        player.gfx("dressing_room")
        return TimerState.CONTINUE
    }

    @TimerStop("dressing_room")
    fun stop(player: Player) {
        player.clearGfx()
        player["delay"] = 1
        player.closeMenu()
        player.gfx("dressing_room_finish")
        player.flagAppearance()
    }
}

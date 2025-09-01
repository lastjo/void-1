package content.entity.player.modal.tab

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.hasMenuOpen
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Spawn

class Options {

    @Interface("Graphics Settings", "graphics", "options")
    fun graphics(player: Player) {
        if (player.hasMenuOpen()) {
            player.message("Please close the interface you have open before setting your graphics options.")
            return
        }
        player.open("graphics_options")
    }

    @Interface("Audio Settings", "audio", "options")
    fun audio(player: Player) {
        if (player.hasMenuOpen()) {
            player.message("Please close the interface you have open before setting your audio options.")
            return
        }
        player.open("audio_options")
    }

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("accept_aid")
    }

    @Interface("Toggle Accept Aid", "aid", "options")
    fun aid(player: Player) {
        player.toggle("accept_aid")
    }

}

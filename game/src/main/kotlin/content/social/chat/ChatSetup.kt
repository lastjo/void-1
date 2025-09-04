package content.social.chat

import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Spawn

class ChatSetup {

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("clan_chat_colour")
        player.sendVariable("private_chat_colour")
    }

    @Interface("Open chat display options", "chat", "options")
    fun open(player: Player) {
        player.open("chat_setup")
    }

    @Interface("No split", "no_split", "chat_setup")
    fun disable(player: Player) {
        player["private_chat_colour"] = -1
    }

    @Interface("Select colour", "clan_colour*", "chat_setup")
    fun colour(player: Player, component: String) {
        val index = component.removePrefix("clan_colour").toInt()
        player["clan_chat_colour"] = index - 1
    }

    @Interface("Select colour", "private_colour*", "chat_setup")
    fun privateColour(player: Player, component: String) {
        val index = component.removePrefix("private_colour").toInt()
        player["private_chat_colour"] = index
    }

    @Interface("Close", "close", "chat_setup")
    fun close(player: Player, component: String) {
        player.open("options")
    }
}

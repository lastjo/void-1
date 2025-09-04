package content.entity.player.command.admin

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.sub.Command

class ObjectCommands(private val objects: GameObjects) {

    @Command("get", description = "get all objects under the player", rights = PlayerRights.ADMIN)
    fun command(player: Player, content: String) {
        objects[player.tile].forEach {
            player.message(it.toString(), ChatType.Console)
        }
    }
}

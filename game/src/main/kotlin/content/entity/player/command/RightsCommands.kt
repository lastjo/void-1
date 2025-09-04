package content.entity.player.command

import com.github.michaelbull.logging.InlineLogger
import net.pearx.kasechange.toSentenceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.entity.character.player.rights
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.get
import world.gregs.voidps.network.client.instruction.ExecuteCommand
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.sub.Command
import world.gregs.voidps.type.sub.Instruction
import world.gregs.voidps.type.sub.Spawn

class RightsCommands {

    private val logger = InlineLogger("RightsCommands")

    @Instruction(ExecuteCommand::class)
    fun findCommand(player: Player, instruction: ExecuteCommand) {
        Publishers.launch {
            try {
                Publishers.all.command(
                    player,
                    instruction.content,
                    instruction.prefix,
                    when (player.rights) {
                        PlayerRights.None -> PlayerRights.NONE
                        PlayerRights.Mod -> PlayerRights.MOD
                        PlayerRights.Admin -> PlayerRights.ADMIN
                    },
                )
            } catch (exception: Exception) {
                logger.warn(exception) { "An error occurred while executing command." }
            }
        }
    }

    @Spawn
    fun spawn(player: Player) {
        if (player.name == Settings.getOrNull("development.admin.name") && player.rights != PlayerRights.Admin) {
            player.rights = PlayerRights.Admin
            player.message("Rights set to Admin. Please re-log to activate.")
        }
    }

    @Command("rights (player-name) (rights-name)", description = "set the rights for another player (None, Mod, Admin)", rights = PlayerRights.ADMIN)
    fun command(player: Player, content: String) {
        val right = content.split(" ").last()
        val rights: PlayerRights
        try {
            rights = PlayerRights.valueOf(right.toSentenceCase())
        } catch (e: IllegalArgumentException) {
            player.message("No rights found with the name: '${right.toSentenceCase()}'.")
            return
        }
        val username = content.removeSuffix(" $right")
        val target = get<Players>().get(username)
        if (target == null) {
            player.message("Unable to find player '$username'.")
        } else {
            target.rights = rights
            player.message("${player.name} rights set to $rights.")
            target.message("${player.name} granted you $rights rights. Please re-log to activate.")
        }
    }
}

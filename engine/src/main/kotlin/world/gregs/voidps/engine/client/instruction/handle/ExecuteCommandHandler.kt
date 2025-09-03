package world.gregs.voidps.engine.client.instruction.handle

import com.github.michaelbull.logging.InlineLogger
import world.gregs.voidps.engine.client.instruction.InstructionHandler
import world.gregs.voidps.engine.client.ui.event.Command
import world.gregs.voidps.engine.client.ui.event.Command.Companion.adminHandlers
import world.gregs.voidps.engine.client.ui.event.Command.Companion.modHandlers
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.isAdmin
import world.gregs.voidps.engine.entity.character.player.isMod
import world.gregs.voidps.engine.entity.character.player.rights
import world.gregs.voidps.engine.event.Events
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.network.client.instruction.ExecuteCommand
import world.gregs.voidps.type.PlayerRights

class ExecuteCommandHandler : InstructionHandler<ExecuteCommand>() {

    private val logger = InlineLogger()

    override fun validate(player: Player, instruction: ExecuteCommand) {
        val handler = if (player.isAdmin()) {
            adminHandlers[instruction.prefix]
        } else if (player.isMod()) {
            modHandlers[instruction.prefix]
        } else {
            return
        }
        Publishers.launch {
            try {
                Publishers.all.command(
                    player, instruction.content, instruction.prefix, when (player.rights) {
                        PlayerRights.None -> PlayerRights.NONE
                        PlayerRights.Mod -> PlayerRights.MOD
                        PlayerRights.Admin -> PlayerRights.ADMIN
                    }
                )
            } catch (exception: Exception) {
                logger.warn(exception) { "An error occurred while executing command." }
            }
        }

        if (handler != null) {
            Events.events.launch {
                try {
                    handler.invoke(Command(player, instruction.prefix, instruction.content), player)
                } catch (exception: Exception) {
                    logger.warn(exception) { "An error occurred while executing command." }
                }
            }
        }
    }
}

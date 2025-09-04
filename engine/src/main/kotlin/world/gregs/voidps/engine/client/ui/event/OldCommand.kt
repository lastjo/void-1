package world.gregs.voidps.engine.client.ui.event

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import world.gregs.voidps.engine.client.ui.chat.Colours
import world.gregs.voidps.engine.client.ui.chat.toTag
import world.gregs.voidps.engine.entity.character.player.Player

class OldCommand {

    companion object {
        val adminHandlers: MutableMap<String, suspend OldCommand.(Player) -> Unit> = Object2ObjectOpenHashMap()
        val modHandlers: MutableMap<String, suspend OldCommand.(Player) -> Unit> = Object2ObjectOpenHashMap()
        var count = 0
        val adminCommands = mutableListOf<String>()
        val modCommands = mutableListOf<String>()
    }
}

fun adminCommand(command: String, description: String = "", aliases: List<String> = emptyList(), block: suspend OldCommand.() -> Unit) {
    if (description.isNotBlank()) {
        OldCommand.adminCommands.add("${Colours.BLUE.toTag()}$command</col>")
    }
    val index = command.indexOfFirst { it == '(' || it == '[' }
    val commandName = (if (index != -1) command.substring(0, index) else command).trim()
    val handler: suspend OldCommand.(Player) -> Unit = {
        block.invoke(this)
    }
    OldCommand.adminHandlers[commandName] = handler
    for (alias in aliases) {
        if (description.isNotBlank()) {
            OldCommand.adminCommands.add("${Colours.BLUE.toTag()}${command.replace(commandName, alias)}</col>")
        }
        OldCommand.adminHandlers[alias] = handler
    }

    if (description.isNotBlank()) {
        OldCommand.adminCommands.add(description)
        OldCommand.adminCommands.add("")
    }
}

fun modCommand(command: String, description: String = "", aliases: List<String> = emptyList(), block: suspend OldCommand.() -> Unit) {
    if (description.isNotBlank()) {
        OldCommand.modCommands.add("${Colours.BLUE.toTag()}$command</col>")
        OldCommand.adminCommands.add("${Colours.BLUE.toTag()}$command</col>")
    }
    val index = command.indexOfFirst { it == '(' || it == '[' }
    val commandName = (if (index != -1) command.substring(0, index) else command).trim()
    val handler: suspend OldCommand.(Player) -> Unit = {
        block.invoke(this)
    }

    OldCommand.modHandlers[commandName] = handler
    OldCommand.adminHandlers[commandName] = handler
    for (alias in aliases) {
        if (description.isNotBlank()) {
            OldCommand.modCommands.add("${Colours.BLUE.toTag()}${command.replace(commandName, alias)}</col>")
            OldCommand.adminCommands.add("${Colours.BLUE.toTag()}${command.replace(commandName, alias)}</col>")
        }
        OldCommand.modHandlers[alias] = handler
        OldCommand.adminHandlers[alias] = handler
    }
    if (description.isNotBlank()) {
        OldCommand.modCommands.add(description)
        OldCommand.adminCommands.add(description)
        OldCommand.modCommands.add("")
        OldCommand.adminCommands.add("")
    }
}

package content.entity.player

import com.github.michaelbull.logging.InlineLogger
import content.entity.effect.transform
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.data.definition.NPCDefinitions
import world.gregs.voidps.engine.data.definition.ObjectDefinitions
import world.gregs.voidps.engine.entity.character.mode.Follow
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.PlayerOptions
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.network.client.instruction.InteractFloorItem
import world.gregs.voidps.network.client.instruction.InteractNPC
import world.gregs.voidps.network.client.instruction.InteractObject
import world.gregs.voidps.network.client.instruction.InteractPlayer
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Instruction

/**
 * Validates and triggers basic entity interactions
 */
class Interaction(
    private val npcs: NPCs,
    private val players: Players,
    private val items: FloorItems,
    private val objects: GameObjects,
    private val npcDefinitions: NPCDefinitions,
    private val objectDefinitions: ObjectDefinitions,
) {

    private val logger = InlineLogger("Interaction")

    @Instruction(InteractFloorItem::class)
    fun floorItem(player: Player, instruction: InteractFloorItem) {
        if (player.contains("delay")) {
            return
        }
        val (id, x, y, optionIndex) = instruction
        val floorItem = validateFloorItem(player, id, x, y) ?: return
        val selectedOption = validateOption(floorItem.def.floorOptions, optionIndex, "floor item", id) ?: return
        if (selectedOption == "Examine") {
            player.message(floorItem.def.getOrNull("examine") ?: return, ChatType.ItemExamine)
            return
        }
        player.closeInterfaces()
        val block: suspend (Boolean) -> Unit = { Publishers.all.playerFloorItemOption(player, floorItem, selectedOption, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasPlayerFloorItemOption(player, floorItem, selectedOption, it) }
        player.mode = Interact(player, floorItem, shape = -1, interact = block, has = check)
    }

    @Instruction(InteractNPC::class)
    fun npc(player: Player, instruction: InteractNPC) {
        if (player.contains("delay")) {
            return
        }
        val npc = npcs.indexed(instruction.npcIndex) ?: return
        val def = if (npc.transform.isBlank()) npc.def else npcDefinitions.get(npc.transform)
        val definition = npcDefinitions.resolve(def, player)
        val selectedOption = validateOption(definition.options, instruction.option - 1, "npc", definition.id) ?: return
        if (selectedOption == "Listen-to" && player["movement", "walk"] == "music") {
            player.message("You are already resting.")
            return
        }
        if (player.hasClock("stunned")) {
            player.message("You're stunned!", ChatType.Filter)
            return
        }
        player.closeInterfaces()
        player.talkWith(npc, definition)
        val block: suspend (Boolean) -> Unit = { Publishers.all.playerNPCOption(player, npc, definition, selectedOption, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasPlayerNPCOption(player, npc, definition, selectedOption, it) }
        player.mode = Interact(player, npc, interact = block, has = check)
    }

    @Instruction(InteractPlayer::class)
    fun player(player: Player, instruction: InteractPlayer) {
        if (player.contains("delay")) {
            return
        }
        val target = players.indexed(instruction.playerIndex) ?: return
        val optionIndex = instruction.option
        val option = player.options.get(optionIndex)
        if (option == PlayerOptions.EMPTY_OPTION) {
            logger.info { "Invalid player option $optionIndex ${player.options.get(optionIndex)} for $player on $target" }
            return
        }
        player.closeInterfaces()
        if (option == "Follow") {
            player.mode = Follow(player, target)
        } else {
            val block: suspend (Boolean) -> Unit = { Publishers.all.playerPlayerOption(player, target, option, it) }
            val check: (Boolean) -> Boolean = { Publishers.all.hasPlayerPlayerOption(player, target, option, it) }
            player.mode = Interact(player, target, interact = block, has = check)
        }
    }

    @Instruction(InteractObject::class)
    fun obj(player: Player, instruction: InteractObject) {
        if (player.contains("delay")) {
            return
        }
        val (objectId, x, y, option) = instruction
        val tile = player.tile.copy(x = x, y = y)
        val target = getObject(tile, objectId)
        if (target == null) {
            logger.warn { "Invalid object $objectId $tile" }
            return
        }
        val definition = objectDefinitions.resolve(target.def, player)
        val selectedOption = validateOption(definition.options, option - 1, "obj", definition.id) ?: return
        player.closeInterfaces()
        val block: suspend (Boolean) -> Unit = { Publishers.all.playerGameObjectOption(player, target, definition, selectedOption, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasPlayerGameObjectOption(player, target, definition, selectedOption, it) }
        player.mode = Interact(player, target, interact = block, has = check)
    }

    private fun validateFloorItem(player: Player, x: Int, y: Int, id: Int): FloorItem? {
        val tile = player.tile.copy(x, y)
        val floorItem = items[tile].firstOrNull { it.def.id == id }
        if (floorItem == null) {
            logger.warn { "Invalid floor item $id $tile" }
            return null
        }
        return floorItem
    }

    private fun validateOption(options: Array<String?>?, index: Int, type: String, id: Int): String? {
        val selectedOption = options?.getOrNull(index)
        if (selectedOption == null) {
            logger.warn { "Invalid $type $id option $index ${options.contentToString()}" }
            return null
        }
        return selectedOption
    }

    private fun getObject(tile: Tile, objectId: Int): GameObject? {
        val obj = objects[tile, objectId]
        if (obj != null) {
            return obj
        }
        val definition = objectDefinitions.getOrNull(objectId)
        return if (definition == null) {
            objects[tile, objectId.toString()]
        } else {
            objects[tile, definition.id]
        }
    }
}

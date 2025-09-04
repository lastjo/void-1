package content.entity.player.dialogue

import com.github.michaelbull.logging.InlineLogger
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.suspend.IntSuspension
import world.gregs.voidps.engine.suspend.NameSuspension
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.network.client.instruction.*
import world.gregs.voidps.type.sub.Continue
import world.gregs.voidps.type.sub.Instruction

class DialogueInput(
    private val interfaceDefinitions: InterfaceDefinitions,
    private val itemDefinitions: ItemDefinitions,
) {

    val logger = InlineLogger("DialogueInput")

    @Instruction(InteractDialogue::class)
    fun validate(player: Player, instruction: InteractDialogue) {
        val (interfaceId, componentId, _) = instruction
        val id = interfaceDefinitions.get(interfaceId).stringId
        if (!player.interfaces.contains(id)) {
            logger.debug { "Dialogue $interfaceId not found for player $player" }
            return
        }

        val component = interfaceDefinitions.get(id).components?.get(componentId)
        if (component == null) {
            logger.debug { "Dialogue $interfaceId component $componentId not found for player $player" }
            return
        }

        Publishers.all.continueDialogue(player, id, component.stringId)
    }

    @Instruction(InteractDialogueItem::class)
    fun validateItem(player: Player, instruction: InteractDialogueItem) {
        val definition = itemDefinitions.getOrNull(instruction.item)
        if (definition == null) {
            logger.debug { "Item ${instruction.item} not found for player $player." }
            return
        }

        Publishers.all.continueDialogueItem(player, item = Item(definition.stringId))
    }

    @Continue("dialogue_npc_chat*", "continue")
    @Continue("dialogue_chat*", "continue")
    @Continue("dialogue_message*", "continue")
    @Continue("dialogue_level_up", "continue")
    @Continue("dialogue_obj_box", "continue")
    @Continue("dialogue_double_obj_box", "continue")
    fun continueDialogue(player: Player) {
        player.continueDialogue()
    }

    @Continue("dialogue_multi*", "line*")
    fun continueDialogueMulti(player: Player, component: String) {
        val choice = component.substringAfter("line").toIntOrNull() ?: -1
        (player.dialogueSuspension as? IntSuspension)?.resume(choice)
    }

    @Continue("dialogue_confirm_destroy")
    fun continueDestroy(player: Player, component: String) {
        (player.dialogueSuspension as? StringSuspension)?.resume(component)
    }

    @Continue("dialogue_skill_creation", "choice*")
    fun continueSkillCreation(player: Player, component: String) {
        val choice = component.substringAfter("choice").toIntOrNull() ?: 0
        (player.dialogueSuspension as? IntSuspension)?.resume(choice - 1)
    }

    @Instruction(EnterInt::class)
    fun int(player: Player, instruction: EnterInt) {
        (player.dialogueSuspension as? IntSuspension)?.resume(instruction.value)
    }

    @Instruction(EnterString::class)
    fun string(player: Player, instruction: EnterString) {
        (player.dialogueSuspension as? StringSuspension)?.resume(instruction.value)
    }

    @Instruction(EnterName::class)
    fun name(player: Player, instruction: EnterName) {
        (player.dialogueSuspension as? NameSuspension)?.resume(instruction.value)
    }
}

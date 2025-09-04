package content.entity.player.dialogue

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.suspend.IntSuspension
import world.gregs.voidps.engine.suspend.NameSuspension
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.network.client.instruction.EnterInt
import world.gregs.voidps.network.client.instruction.EnterName
import world.gregs.voidps.network.client.instruction.EnterString
import world.gregs.voidps.type.sub.Continue
import world.gregs.voidps.type.sub.Instruction

class DialogueInput {

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

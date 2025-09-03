package content.entity.player.dialogue

import world.gregs.voidps.engine.client.instruction.instruction
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.suspend.IntSuspension
import world.gregs.voidps.engine.suspend.NameSuspension
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.network.client.instruction.EnterInt
import world.gregs.voidps.network.client.instruction.EnterName
import world.gregs.voidps.network.client.instruction.EnterString
import world.gregs.voidps.type.sub.Continue

class DialogueInput {

    init {
        instruction<EnterInt> { player ->
            (player.dialogueSuspension as? IntSuspension)?.resume(value)
        }

        instruction<EnterString> { player ->
            (player.dialogueSuspension as? StringSuspension)?.resume(value)
        }

        instruction<EnterName> { player ->
            (player.dialogueSuspension as? NameSuspension)?.resume(value)
        }
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
}

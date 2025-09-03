package content.bot.skill

import content.bot.isBot
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.network.client.instruction.InteractDialogue
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Open

class SkillBot {

    @Open("dialogue_level_up")
    fun open(player: Player) {
        if (player.isBot) {
            player.instructions.trySend(InteractDialogue(interfaceId = 740, componentId = 3, option = -1))
        }
    }
}

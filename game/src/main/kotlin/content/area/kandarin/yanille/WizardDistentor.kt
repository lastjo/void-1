package content.area.kandarin.yanille

import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.questCompleted
import content.skill.runecrafting.EssenceMine
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class WizardDistentor {

    @Option("Talk-to", "wizard_distentor")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Talk>("Welcome to the Magicians' Guild!")
        if (!player.questCompleted("rune_mysteries")) {
            return@talkWith
        }
        player<Talk>("Hello there.")
        npc<Quiz>("What can I do for you?")
        choice {
            option<Talk>("Nothing thanks, I'm just looking around.") {
                npc<Talk>("That's fine with me.")
            }
            option<Quiz>("Can you teleport me to the Rune Essence Mine?") {
                EssenceMine.teleport(target, player)
            }
        }
    }

    @Option("Teleport", "wizard_distentor")
    fun teleport(player: Player, target: NPC) {
        if (player.questCompleted("rune_mysteries")) {
            EssenceMine.teleport(target, player)
        } else {
            player.message("You need to have completed the Rune Mysteries Quest to use this feature.")
        }
    }

}

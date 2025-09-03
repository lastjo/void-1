package content.area.kharidian_desert.al_kharid.duel_arena

import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.Uncertain
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class SurgeonGeneralTafani {

    @Option("Talk-to", "surgeon_general_tafani")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        player<Happy>("Hi!")
        npc<Happy>("Hi. How can I help?")
        menu()
    }

    @Option("Heal", "surgeon_general_tafani")
    suspend fun heal(player: Player, npc: NPC) = player.talkWith(npc) {
        heal()
    }

    suspend fun Dialogue.menu() {
        choice {
            option<Uncertain>("Can you heal me?") {
                heal()
            }
            fighters()
            often()
            option<Quiz>("Can you tell me about your cape?") {
                npc<Happy>("Certainly! Skillcapes are a symbol of achievement. Only people who have mastered a skill and reached level 99 can get their hands on them and gain the benefits they carry.")
                npc<Neutral>("The Cape of Constitution doubles the speed of your constitution replenishing when worn. Is there anything else I can help you with?")
                menu()
            }
        }
    }
}

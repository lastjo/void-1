package content.area.kharidian_desert.al_kharid.duel_arena

import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.npcOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class Jaraah {

    @Option("Talk-to", "jaraah")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        player<Happy>("Hi!")
        npc<Frustrated>("What? Can't you see I'm busy?!")
        choice {
            option<Uncertain>("Can you heal me?") {
                heal()
            }
            option<Uncertain>("You must see some gruesome things?") {
                npc<Frustrated>("It's a gruesome business and with the tools they give me it gets more gruesome before it gets better!")
                player<Chuckle>("Really?")
                npc<Chuckle>("It beats being stuck in the monastery!")
            }
            option<Uncertain>("Why do they call you 'The Butcher'?") {
                npc<Chuckle>("'The Butcher'?")
                npc<Frustrated>("Ha!")
                npc<Frustrated>("Would you like me to demonstrate?")
                player<Surprised>("Er...I'll give it a miss, thanks.")
            }
        }
    }

    @Option("Heal", "jaraah")
    suspend fun heal(player: Player, target: NPC) = player.talkWith(target) {
        heal()
    }
}

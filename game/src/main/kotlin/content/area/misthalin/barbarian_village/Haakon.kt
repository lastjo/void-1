package content.area.misthalin.barbarian_village

import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.quest.quest
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.Option

class Haakon {

    private val validStages = setOf("tell_gudrun", "write_poem", "more_poem", "one_more_poem", "poem_done", "poem", "recital", "gunnars_ground")

    @Option("Talk-to", "haakon_the_champion")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Angry>("I am Haakon, champion of this village. Do you seek to challenge me?")
        choice {
            option<Neutral>("I challenge you!") {
                attack()
            }
            if (validStages.contains(player.quest("gunnars_ground"))) {
                option<Neutral>("You argued with Gunthor.") {
                    npc<Frustrated>("There is no argument. I honour my father and my ancestors.")
                    choice {
                        option<Neutral>("Don't you want to settle permanently?") {
                            npc<Angry>("You test my patience by quuestioning my loyalty to my chieftain. Take up my challenge, outerlander, that I might honourably split your skull open..")
                            choice {
                                option<Neutral>("I'll take your challenge!") {
                                    attack()
                                }
                                option<Neutral>("No thanks.")
                            }
                        }
                        option<Neutral>("How about that challenge?") {
                            attack()
                        }
                        option<Neutral>("Goodbye then.")
                    }
                }
            }
            option<Amazed>("Er, no.")
        }
    }

    suspend fun Dialogue.attack() {
        npc<Mad>("Make peace with your god, outerlander!")
        val block: suspend (Boolean) -> Unit = { Publishers.all.npcPlayerOption(target, player, "Attack", it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasNPCPlayerOption(target, player, "Attack", it) }
        target.mode = Interact(target, player, interact = block, has = check)
    }
}

package content.entity.death

import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.characterLevelChange
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script

@Script
class CharacterDeath {

    val publishers: Publishers by inject()

    init {
        characterLevelChange(Skill.Constitution) { character ->
            if (to <= 0 && !character.queue.contains("death")) {
                when (character) {
                    is Player -> publishers.playerDeath(character)
                    is NPC -> publishers.npcDeath(character)
                }
                publishers.characterDeath(character)
                character.emit(Death)
            }
        }
    }
}

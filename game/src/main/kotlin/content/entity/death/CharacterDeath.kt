package content.entity.death

import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.LevelChange

class CharacterDeath {

    @LevelChange(Skill.CONSTITUTION)
    fun levelChange(character: Character, to: Int) {
        if (to <= 0 && !character.queue.contains("death")) {
            when (character) {
                is Player -> Publishers.all.playerDeath(character)
                is NPC -> Publishers.all.npcDeath(character)
            }
            Publishers.all.characterDeath(character)
            character.emit(Death)
        }
    }
}

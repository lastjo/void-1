package content.entity.player

import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.move.characterMove
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Move

class ForceMovement {
    @Move
    fun move(character: Character) {
        val block: () -> Unit = character.remove("force_walk") ?: return
        block.invoke()
    }
}

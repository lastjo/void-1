package content.entity.player.modal.tab

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Open

class Notes {

    @Open("notes")
    fun open(player: Player, id: String) {
        player.interfaceOptions.unlockAll(id, "notes", 0..30)
    }
}

package world.gregs.voidps.engine.client.instruction.handle

import world.gregs.voidps.engine.client.instruction.InstructionHandler
import world.gregs.voidps.engine.client.ui.SongEndEvent
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.network.client.instruction.SongEnd

class SongEndHandler : InstructionHandler<SongEnd>() {
    override fun validate(player: Player, instruction: SongEnd) {
        val songIndex = instruction.songIndex
        Publishers.all.publishPlayer(player, "song_end", songIndex.toString())
    }
}

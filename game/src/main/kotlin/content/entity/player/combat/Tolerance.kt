package content.entity.player.combat

import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.timer.epochSeconds
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Move
import world.gregs.voidps.type.sub.Spawn
import java.util.concurrent.TimeUnit

class Tolerance {

    /**
     * Certain NPCs stop being aggressive towards the player if they stay inside their tolerance area for [toleranceTime]
     */
    private val toleranceTime = TimeUnit.MINUTES.toSeconds(10)

    @Spawn
    fun spawn(player: Player) {
        if (!player.contains("tolerance")) {
            player.start("tolerance", toleranceTime.toInt(), epochSeconds())
        }
        player["tolerance_area"] = player.tile.toCuboid(10)
    }

    @Move
    fun set(player: Player, to: Tile) {
        if (to in player.getOrPut("tolerance_area") { player.tile.toCuboid(10) }) {
            return
        }
        player["tolerance_area"] = player.tile.toCuboid(10)
        player.start("tolerance", toleranceTime.toInt(), epochSeconds())
    }
}

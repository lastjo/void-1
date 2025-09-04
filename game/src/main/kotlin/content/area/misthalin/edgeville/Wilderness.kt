package content.area.misthalin.edgeville

import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Move
import world.gregs.voidps.type.sub.Spawn

class Wilderness(areas: AreaDefinitions) {

    private val wilderness = areas["wilderness"]
    private val safeZones = areas.getTagged("safe_zone")

    @Spawn
    fun spawn(player: Player) {
        if (inWilderness(player.tile)) {
            player["in_wilderness"] = true
        }
    }

    @Move
    fun move(player: Player, from: Tile, to: Tile) {
        val wasIn = inWilderness(from)
        val nowIn = inWilderness(to)
        when {
            !wasIn && nowIn -> player["in_wilderness"] = true
            wasIn && !nowIn -> player.clear("in_wilderness")
        }
    }

    fun inWilderness(tile: Tile) = tile in wilderness && safeZones.none { tile in it.area }
}

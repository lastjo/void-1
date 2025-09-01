package content.area.misthalin

import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option

class Signposts {

    @Option("Read", "direction_signpost_*")
    fun operate(player: Player, target: GameObject) {
        val locations = target.def.extras?.get("locations") as? List<Map<String, String>> ?: return

        val location =
            locations.firstOrNull {
                Tile(it.getOrDefault("x", "0").toInt(), it.getOrDefault("y", "0").toInt()) == target.tile
            } ?: return

        player.open("signpost_directions")
        player.interfaces.sendText("signpost_directions", "north", location.getOrDefault("north_text", ""))
        player.interfaces.sendText("signpost_directions", "east", location.getOrDefault("east_text", ""))
        player.interfaces.sendText("signpost_directions", "south", location.getOrDefault("south_text", ""))
        player.interfaces.sendText("signpost_directions", "west", location.getOrDefault("west_text", ""))
    }

}

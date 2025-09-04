package content.area.misthalin.lumbridge.roddecks_house

import content.entity.sound.areaSound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.sub.Option
import java.util.concurrent.TimeUnit

class RoddecksHouseDrawers {

    @Option("Open", "lumbridge_drawers_closed")
    fun open(player: Player, target: GameObject) {
        player.anim("open_chest")
        areaSound("drawer_open", target.tile)
        target.replace(target.id.replace("_closed", "_opened"), ticks = TimeUnit.MINUTES.toTicks(3))
    }

    @Option("Close", "lumbridge_drawers_opened")
    fun close(player: Player, target: GameObject) {
        player.anim("close_chest")
        areaSound("drawer_close", target.tile)
        target.replace(target.id.replace("_opened", "_closed"), ticks = TimeUnit.MINUTES.toTicks(3))
    }

    @Option("Search", "lumbridge_drawers_opened")
    fun search(player: Player, target: GameObject) {
        player.message("Nothing terribly interesting in here.")
    }
}

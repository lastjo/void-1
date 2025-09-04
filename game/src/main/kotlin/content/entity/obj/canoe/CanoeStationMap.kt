package content.entity.obj.canoe

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface

class CanoeStationMap {

    @Close("canoe_stations_map")
    fun close(player: Player) {
        player.dialogueSuspension = null
    }

    @Interface("Select", "travel_*", "canoe_stations_map")
    fun select(player: Player, component: String) {
        val destination = component.removePrefix("travel_")
        (player.dialogueSuspension as? StringSuspension)?.resume(destination)
    }
}

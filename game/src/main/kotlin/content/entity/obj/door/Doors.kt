package content.entity.obj.door

import content.entity.obj.door.Door.closeDoor
import content.entity.obj.door.Door.isDoor
import content.entity.obj.door.Door.openDoor
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.timer.epochSeconds
import world.gregs.voidps.type.sub.Option

class Doors {

    // Times a door can be closed consecutively before getting stuck
    private val doorStuckCount = 5

    @Option("Close")
    fun close(player: Player, target: GameObject, def: ObjectDefinition) {
        if (!def.isDoor()) {
            return
        }
        // Prevent players from trapping one another
        if (stuck(player)) {
            return
        }
        closeDoor(player, target, def)
    }

    @Option("Open")
    suspend fun open(player: Player, target: GameObject, def: ObjectDefinition) {
        if (!def.isDoor()) {
            return
        }
        if (openDoor(player, target, def)) {
            player.delay(0)
            Publishers.all.publishPlayer(player, "door_opened")
        }
    }

    fun stuck(player: Player): Boolean {
        if (player.remaining("stuck_door", epochSeconds()) > 0) {
            player.message("The door seems to be stuck.")
            return true
        }
        if (player.hasClock("recently_opened_door")) {
            if (player.inc("door_slam_count") >= doorStuckCount) {
                player.start("stuck_door", 60, epochSeconds())
                return true
            }
        } else {
            player.clear("door_slam_count")
        }
        player.start("recently_opened_door", 10)
        return false
    }
}

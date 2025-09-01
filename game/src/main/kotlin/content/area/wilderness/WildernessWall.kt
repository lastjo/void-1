package content.area.wilderness

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.equals
import world.gregs.voidps.type.sub.Option

class WildernessWall {

    @Option("Cross", "wilderness_wall_*")
    suspend fun operate(player: Player, target: GameObject) {
        if (target.id == "wilderness_wall_1" && target.tile.equals(2996, 3531)) {
            val direction = if (player.tile.x < target.tile.x) Direction.EAST else Direction.WEST
            player.anim("wild_ditch_jump")
            player.exactMoveDelay(player.tile.copy(x = target.tile.x + if (direction == Direction.EAST) 2 else -1), delay = 60, direction = direction)
            return
        }
        val direction = if (player.tile.y < target.tile.y) Direction.NORTH else Direction.SOUTH
        player.anim("wild_ditch_jump")
        player.exactMoveDelay(player.tile.copy(y = target.tile.y + if (direction == Direction.NORTH) 2 else -1), delay = 60, direction = direction)
    }
}

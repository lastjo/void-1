package content.skill.agility.shortcut

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.noInterest
import world.gregs.voidps.engine.entity.character.player.chat.obstacle
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.sub.Option

class Stiles {

    @Option("Climb-over", "freds_farm_stile")
    @Option("Climb-over", "catherby_stile")
    @Option("Climb-over", "death_plateau_stile")
    @Option("Climb-over", "falconry_area_stile")
    suspend fun climbNorth(player: Player, target: GameObject) {
        climbStile(player, target, Direction.NORTH)
    }

    @Option("Climb-over", "ardougne_farm_stile")
    suspend fun climbEast(player: Player, target: GameObject) {
        climbStile(player, target, Direction.EAST)
    }

    @Option("Climb-over", "falador_farm_stile")
    suspend fun climbFaladorFarm(player: Player, target: GameObject) {
        val rotation = when (target.rotation) {
            2 -> Direction.NORTH
            3 -> Direction.EAST
            else -> return player.noInterest()
        }
        climbStile(player, target, rotation)
    }

    @Option("Climb-over", "vinesweeper_stile")
    suspend fun climbVinesweeper(player: Player, target: GameObject) {
        val rotation = when (target.rotation) {
            0, 2 -> Direction.NORTH
            1, 3 -> Direction.EAST
            else -> return player.noInterest()
        }
        climbStile(player, target, rotation)
    }

    @Option("Climb-over", "vinesweeper_stile")
    suspend fun climbFaladorWall(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 5)) {
            player.obstacle(5)
            return
        }
        climbStile(player, target, Direction.EAST)
        player.exp(Skill.Agility, 0.5)
    }

    suspend fun climbStile(player: Player, target: GameObject, rotation: Direction) {
        val direction = when (rotation) {
            Direction.NORTH -> if (player.tile.y > target.tile.y) Direction.SOUTH else Direction.NORTH
            Direction.SOUTH -> if (player.tile.y < target.tile.y) Direction.NORTH else Direction.SOUTH
            Direction.EAST -> if (player.tile.x > target.tile.x) Direction.WEST else Direction.EAST
            Direction.WEST -> if (player.tile.x < target.tile.x) Direction.EAST else Direction.WEST
            else -> return player.noInterest()
        }
        val start = if (direction == rotation) target.tile else target.tile.minus(direction)
        player.walkOverDelay(start)
        player.face(direction)
        player.delay()
        player.anim("rocks_pile_climb")
        val tile = if (direction == rotation) target.tile.add(direction) else target.tile
        player.exactMoveDelay(tile, 30, direction = direction)
    }
}

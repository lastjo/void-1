package content.skill.agility.shortcut

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option

class UnderWallTunnels {

    @Option("Climb-into", "yanille_underwall_tunnel_hole")
    suspend fun yanilleTunnel(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 15,
            start = Tile(2575, 3112),
            end = Tile(2575, 3108),
            direction = Direction.SOUTH,
        )
    }

    @Option("Climb-under", "yanille_underwall_tunnel_castle_wall")
    suspend fun yanilleCastle(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 15,
            start = Tile(2575, 3107),
            end = Tile(2575, 3111),
            direction = Direction.NORTH,
        )
    }

    @Option("Climb-into", "edgeville_underwall_tunnel")
    suspend fun edgeville(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 21,
            start = Tile(3138, 3516),
            end = Tile(3143, 3514),
            direction = Direction.EAST,
        )
    }

    @Option("Climb-into", "grand_exchange_underwall_tunnel")
    suspend fun grandExchange(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 21,
            start = Tile(3144, 3514),
            end = Tile(3139, 3516),
            direction = Direction.WEST,
        )
    }

    @Option("Climb-into", "falador_underwall_tunnel_north")
    suspend fun faladorNorth(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 26,
            start = Tile(2948, 3313),
            end = Tile(2948, 3310),
            direction = Direction.SOUTH,
        )
    }

    @Option("Climb-into", "falador_underwall_tunnel_south")
    suspend fun faladorSouth(player: Player, target: GameObject) {
        tunnel(
            player = player,
            level = 26,
            start = Tile(2948, 3309),
            end = Tile(2948, 3312),
            direction = Direction.NORTH,
        )
    }

    suspend fun tunnel(player: Player, level: Int, start: Tile, end: Tile, direction: Direction) {
        if (!player.has(Skill.Agility, level)) {
            player.message("You need an Agility level of $level to negotiate this tunnel.")
            return
        }
        player.walkToDelay(start)
        player.clear("face_entity")
        player.face(direction)
        player.delay()
        player.anim("climb_into_tunnel")
        player.exactMoveDelay(start.add(direction), 50, direction)
        player.anim("tunnel_invisible")
        player.exactMoveDelay(end, 100, direction)
        player.delay()
        player.anim("climb_out_of_tunnel")
        player.exactMoveDelay(end.add(direction), startDelay = 15, delay = 33, direction = direction)
    }
}

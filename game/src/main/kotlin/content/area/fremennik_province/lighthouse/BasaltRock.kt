package content.area.fremennik_province.lighthouse

import content.entity.combat.hit.damage
import content.entity.gfx.areaGfx
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Level
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.ObjectOption
import world.gregs.voidps.engine.entity.obj.objectApproach
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.equals
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Option

class BasaltRock {

    private val data = mapOf(
        "beach" to Triple(Tile(2522, 3595), Direction.NORTH, false),
        "basalt_rock_start" to Triple(Tile(2522, 3597), Direction.SOUTH, false),
        "basalt_rock_2" to Triple(Tile(2522, 3600), Direction.NORTH, true),
        "basalt_rock_3" to Triple(Tile(2522, 3602), Direction.SOUTH, true),
        "basalt_rock_4" to Triple(Tile(2518, 3611), Direction.WEST, true),
        "basalt_rock_5" to Triple(Tile(2516, 3611), Direction.EAST, true),
        "basalt_rock_6" to Triple(Tile(2514, 3613), Direction.NORTH, true),
        "basalt_rock_7" to Triple(Tile(2514, 3615), Direction.SOUTH, true),
        "basalt_rock_end" to Triple(Tile(2514, 3617), Direction.NORTH, false),
        "rocky_shore" to Triple(Tile(2514, 3619), Direction.SOUTH, false),
    )

    @Option("Jump-to", "beach")
    @Option("Jump-across", "basalt_rock_start")
    @Option("Jump-across", "basalt_rock_2")
    @Option("Jump-across", "basalt_rock_3")
    @Option("Jump-across", "basalt_rock_4")
    @Option("Jump-across", "basalt_rock_5")
    @Option("Jump-across", "basalt_rock_6")
    @Option("Jump-across", "basalt_rock_7")
    @Option("Jump-across", "basalt_rock_end")
    @Option("Jump-to", "rocky_shore")
    suspend fun jump(player: Player, target: GameObject) {
        val (tile, direction, exp) = data[target.id] ?: return
        jump(player, target, tile.add(direction).add(direction), direction, exp)
    }

    @Option("Jump-to", "beach", approach = true)
    @Option("Jump-across", "basalt_rock_start", approach = true)
    @Option("Jump-across", "basalt_rock_2", approach = true)
    @Option("Jump-across", "basalt_rock_3", approach = true)
    @Option("Jump-across", "basalt_rock_4", approach = true)
    @Option("Jump-across", "basalt_rock_5", approach = true)
    @Option("Jump-across", "basalt_rock_6", approach = true)
    @Option("Jump-across", "basalt_rock_7", approach = true)
    @Option("Jump-across", "basalt_rock_end", approach = true)
    @Option("Jump-to", "rocky_shore", approach = true)
    suspend fun approach(player: Player, target: GameObject) {
        val (tile, direction, exp) = data[target.id] ?: return
        val sameSide = when (direction) {
            Direction.NORTH -> player.tile.y <= target.tile.y
            Direction.EAST -> player.tile.x <= target.tile.x
            Direction.SOUTH -> player.tile.y >= target.tile.y
            Direction.WEST -> player.tile.x >= target.tile.x
            else -> false
        }
        if (sameSide) {
            jump(player, target, tile.add(direction).add(direction), direction, exp)
        } else {
            jump(player, target, target.tile, direction.inverse(), exp)
        }
    }

    suspend fun jump(player: Player, target: GameObject, opposite: Tile, direction: Direction, exp: Boolean) {
        player.walkToDelay(target.tile)
        player.clear("face_entity")
        // Fail on jump
        val fail = when {
            player.tile.equals(2522, 3600) -> Tile(2521, 3596)
            player.tile.equals(2514, 3615) -> Tile(2515, 3618)
            else -> null
        }
        if (fail == null || Level.success(player.levels.get(Skill.Agility), 5..255)) {
            player.anim("stepping_stone_step", delay = 19)
            player.sound("jump", delay = 35)
            player.exactMoveDelay(opposite, startDelay = 47, delay = 59, direction = direction)
            if (exp) {
                player.exp(Skill.Agility, 2.0)
            }
        } else {
            player.message("You slip on the slimy causeway.")
            player.anim("rope_walk_fall_left")
            val fall = player.tile.copy(x = fail.x)
            player.exactMoveDelay(fall, startDelay = 33, delay = 53, direction = direction)
            player.renderEmote("swim")
            areaGfx("big_splash", fall, delay = 3)
            player.sound("pool_plop")
            player.walkOverDelay(fail)
            player.message("The tide sweeps you back to shore.")
            player.clearRenderEmote()
            player.walkOverDelay(fail.add(direction.inverse()))
            player.damage(random.nextInt(100))
            if (exp) {
                player.exp(Skill.Agility, 0.5)
            }
        }
    }
}

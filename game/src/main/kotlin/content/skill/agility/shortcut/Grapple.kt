package content.skill.agility.shortcut

import content.entity.gfx.areaGfx
import content.entity.player.dialogue.type.statement
import content.entity.sound.sound
import content.skill.melee.weapon.Weapon
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.*
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option

class Grapple(
    private val objects: GameObjects,
    private val areas: AreaDefinitions,
) {

    @Option("Grapple", "lumbridge_broken_raft", approach = true)
    suspend fun grappleRaft(player: Player, target: GameObject) {
        if (!player.hasRequirements(ranged = 37, agility = 8, strength = 17)) {
            return
        }
        player.steps.clear()
        val direction = if (player.tile.x < 3253) Direction.EAST else Direction.WEST
        // Skip first half if player is stuck on raft somehow
        if (player.tile.distanceTo(target.tile) > 2) {
            val start = if (direction == Direction.EAST) Tile(3246, 3179) else Tile(3259, 3180)
            if (player.tile.distanceTo(start) > 1) {
                player.message("I can't do that from here, get closer.")
                return
            }
            player.face(target)
            player.delay(2)
            player.anim("crossbow_accurate")
            player.sound("grapple_shoot")
            player.delay(3)
            player.message("You successfully grapple the raft and tie the rope to a tree.", ChatType.Filter)
            if (direction == Direction.EAST) {
                lumbridgeTree(grapple = false)
            } else {
                alKharidTree(grapple = false)
            }
            player.walkOverDelay(start.add(direction))
            player.anim("grapple_enter_water")
            areaGfx("big_splash", start.addX(direction.delta.x * 2), delay = 6)
            player.sound("grapple_splash", 3)
            player.exactMoveDelay(start.addX(direction.delta.x * 6), 120, direction)
        }
        if (direction == Direction.EAST) {
            player.walkToDelay(Tile(3252, 3180))
            player.walkToDelay(Tile(3253, 3180))
            player.face(Tile(3260, 3180))
        } else {
            player.walkToDelay(Tile(3252, 3180))
            player.face(Tile(3244, 3179))
        }
        player.delay(2)
        player.anim("crossbow_accurate")
        player.sound("grapple_shoot")
        player.delay(3)
        player.message("You successfully grapple the tree on the opposite bank.", ChatType.Filter)
        if (direction == Direction.EAST) {
            alKharidTree(grapple = true)
        } else {
            lumbridgeTree(grapple = true)
        }
        player.delay()
        player.anim("grapple_exit_water")
        areaGfx("big_splash", player.tile.add(direction), delay = 6)
        player.sound("grapple_splash", 3)
        val shore = if (direction == Direction.EAST) Tile(3258, 3180) else Tile(3248, 3179)
        player.exactMoveDelay(shore, 160, direction)
        val end = if (direction == Direction.EAST) Tile(3259, 3180) else Tile(3246, 3179)
        player.walkOverDelay(end)
    }

    @Option("Grapple", "falador_wall_north")
    suspend fun grappleNorthWall(player: Player, target: GameObject) {
        player.walkToDelay(Tile(3006, 3395))
        player.face(Direction.SOUTH)
        player.delay()
        if (!player.hasRequirements(ranged = 19, agility = 11, strength = 37)) {
            return
        }
        player.anim("grapple_wall_climb")
        player.gfx("grapple_wall_climb")
        player.sound("grapple_shoot", delay = 45)
        player.delay(11)
        player.clearGfx()
        player.clearAnim()
        player.tele(3006, 3394, 1)
    }

    @Option("Grapple", "falador_wall_south")
    suspend fun grappleSouthWall(player: Player, target: GameObject) {
        player.walkToDelay(Tile(3005, 3393))
        player.face(Direction.NORTH)
        player.delay()
        if (!player.hasRequirements(ranged = 19, agility = 11, strength = 37)) {
            return
        }
        player.anim("grapple_wall_climb")
        player.gfx("grapple_wall_climb")
        player.sound("grapple_shoot", delay = 45)
        player.delay(11)
        player.clearGfx()
        player.clearAnim()
        player.tele(3005, 3394, 1)
    }

    @Option("Jump", "falador_wall_jump_north")
    suspend fun jumpNorthWall(player: Player, target: GameObject) {
        player.walkToDelay(Tile(3006, 3394, 1))
        if (!player.has(Skill.Agility, 4)) {
            player.message("You need an agility level of at least 4 to climb down this wall.")
            return
        }
        player.anim("jump_down")
        player.delay(1)
        player.anim("jump_land")
        player.tele(3006, 3395, 0)
    }

    @Option("Jump", "falador_wall_jump_south")
    suspend fun jumpSouthWall(player: Player, target: GameObject) {
        player.walkToDelay(Tile(3005, 3394, 1))
        if (!player.has(Skill.Agility, 4)) {
            player.message("You need an agility level of at least 4 to climb down this wall.")
            return
        }
        player.anim("jump_down")
        player.delay(1)
        player.anim("jump_land")
        player.tele(3005, 3393, 0)
    }

    @Option("Grapple", "catherby_crossbow_tree", approach = true)
    suspend fun grappleTree(player: Player, target: GameObject) {
        if (!player.hasRequirements(ranged = 39, agility = 36, strength = 22)) {
            return
        }
        player.steps.clear()
        val start = Tile(2841, 3425)
        if (player.tile !in areas["water_obselisk_island"]) {
            player.message("I can't do that from here.")
            return
        }
        player.walkToDelay(start)
        player.face(Direction.NORTH)
        player.delay()
        player.anim("grapple_aim_fire")
        player.delay(2)
        player.anim("crossbow_accurate")
        player.sound("grapple_shoot")
        player.delay(3)
        for (y in 3427..3433) {
            objects.add("grapple_rope", Tile(2841, y), rotation = 1, shape = ObjectShape.GROUND_DECOR, ticks = 14)
        }
        objects.add("catherby_rocks_rope", Tile(2841, 3426), rotation = 1, shape = ObjectShape.GROUND_DECOR, ticks = 14)
        target.replace("catherby_crossbow_tree_grapple", ticks = 14)
        player.delay(4)
        player.anim("water_obelisk_swim")
        areaGfx("big_splash", Tile(2841, 3428), 6)
        player.sound("grapple_splash", delay = 6)
        player.exactMoveDelay(Tile(2841, 3432), delay = 160, direction = Direction.NORTH)
    }

    @Option("Grapple", "catherby_rocks", approach = true)
    suspend fun grappleRocks(player: Player, target: GameObject) {
        if (!player.hasRequirements(ranged = 35, agility = 32, strength = 35)) {
            return
        }
        player.steps.clear()
        if (player.tile !in areas["mountain_shortcut_grapple_area"]) {
            player.message("I can't do that from here.")
            return
        }
        player.walkToDelay(Tile(2866, 3429))
        player.face(Direction.EAST)
        player.delay()
        player.anim("grapple_aim_fire")
        player.sound("grapple_shoot", delay = 55)
        player.delay(2)
        player.renderEmote("climbing")
        for (x in 2867..2869) {
            objects.add("catherby_grapple_rope", Tile(x, 3429), shape = ObjectShape.GROUND_DECOR, ticks = 14)
        }
        objects.add("catherby_rocks_grapple", Tile(2869, 3429), shape = ObjectShape.GROUND_DECOR, ticks = 14)
        player.delay()
        player.walkOverDelay(Tile(2868, 3429))
        player.clearRenderEmote()
        player.walkOverDelay(Tile(2869, 3430))
    }

    @Option("Grapple", "yanille_grapple_wall")
    suspend fun grappleWall(player: Player, target: GameObject) {
        val direction = if (player.tile.y >= target.tile.y) Direction.SOUTH else Direction.NORTH
        player.walkToDelay(target.tile)
        player.face(direction)
        player.delay()
        if (!player.hasRequirements(ranged = 21, agility = 39, strength = 38)) {
            return
        }
        player.anim("grapple_wall_climb")
        player.gfx("grapple_wall_climb")
        player.sound("grapple_shoot", delay = 45)
        player.delay(11)
        player.clearGfx()
        player.clearAnim()
        var dest = target.tile
        if (direction != Direction.NORTH) {
            dest = target.tile.add(direction)
        }
        player.tele(dest.copy(level = 1))
    }

    @Option("Jump", "yanille_grapple_wall_jump")
    suspend fun jumpWall(player: Player, target: GameObject) {
        val direction = if (player.tile.y == target.tile.y) Direction.SOUTH else Direction.NORTH
        player.walkToDelay(target.tile)
        if (!player.has(Skill.Agility, 4)) {
            player.message("You need an agility level of at least 4 to climb down this wall.")
            return
        }
        player.anim("jump_down")
        player.delay(1)
        player.anim("jump_land")
        var dest = target.tile
        if (direction == Direction.SOUTH) {
            dest = target.tile.add(direction)
        }
        player.tele(dest.copy(level = 0))
    }

    fun Grapple.lumbridgeTree(grapple: Boolean) {
        val tree = objects[Tile(3244, 3179), "strong_yew"]
        tree?.replace("strong_yew_${if (grapple) "grapple" else "rope"}", ticks = 8)
        for (x in 3246..3251) {
            objects.add("grapple_rope", Tile(x, 3179), shape = ObjectShape.GROUND_DECOR, ticks = 8)
        }
    }

    fun Grapple.alKharidTree(grapple: Boolean) {
        val tree = objects[Tile(3260, 3179), "strong_tree"]
        tree?.replace("strong_tree_${if (grapple) "grapple" else "rope"}", ticks = 8)
        for (x in 3254..3259) {
            objects.add("grapple_rope", Tile(x, 3180), shape = ObjectShape.GROUND_DECOR, ticks = 8)
        }
    }

    suspend fun Player.hasRequirements(ranged: Int, agility: Int, strength: Int): Boolean {
        if (!has(Skill.Ranged, ranged) || !has(Skill.Agility, agility) || !has(Skill.Strength, strength)) {
            dialogue { statement("You need at least $ranged Ranged, $agility Agility and $strength Strength to do that.") }
            return false
        }
        return Weapon.hasGrapple(this)
    }
}

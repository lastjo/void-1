package content.area.troll_country.trollheim

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.equals
import world.gregs.voidps.type.sub.Option

class Trollheim {

    @Option("Climb", "trollheim_rocks")
    suspend fun climbRocks(player: Player, target: GameObject) {
        player.face(target)
        if (target.tile.equals(2901, 3680)) {
            player.anim("human_climbing_down")
            val tile = Tile(2903, 3680)
            player.exactMoveDelay(tile, 120, direction = Direction.WEST)
            player.exp(Skill.Agility, 1.0)
        } else if (target.tile.equals(2902, 3680)) {
            val tile = Tile(2900, 3680)
            player.renderEmote("climbing")
            player.walkOverDelay(tile)
            player.clearRenderEmote()
            player.exp(Skill.Agility, 8.0)
        }
    }

    @Option("Climb", "trollheim_rocks_4")
    suspend fun climbRocks4(player: Player, target: GameObject) {
        player.face(target)
        if (target.tile.equals(2885, 3684)) {
            if (player.tile.x >= target.tile.x) {
                player.anim("rocks_climb_down")
                player.exactMoveDelay(target.tile.copy(x = 2884), 50, direction = Direction.EAST)
                player.exp(Skill.Agility, 1.0)
                player.clearAnim()
            } else {
                player.renderEmote("climbing")
                player.walkOverDelay(target.tile.copy(x = 2886))
                player.exp(Skill.Agility, 8.0)
                player.clearRenderEmote()
            }
        } else if (target.tile.equals(2887, 3661)) {
            if (player.tile.y >= target.tile.y) {
                player.anim("rocks_climb_down", delay = 15)
                player.exactMoveDelay(target.tile.addY(-1), 50, direction = Direction.NORTH)
                player.exp(Skill.Agility, 1.0)
                player.clearAnim()
            } else {
                player.renderEmote("climbing")
                player.walkOverDelay(target.tile.addY(1))
                player.exp(Skill.Agility, 1.0)
                player.clearRenderEmote()
            }
        }
    }

    @Option("Climb", "trollheim_rocks_5")
    suspend fun climbRocks5(player: Player, target: GameObject) {
        player.face(target)
        if (target.tile.equals(2885, 3683)) {
            if (player.tile.x >= target.tile.x) {
                player.anim("rocks_climb_down")
                player.exactMoveDelay(target.tile.addX(-1), 50, direction = Direction.EAST)
                player.exp(Skill.Agility, 1.0)
                player.clearAnim()
            } else {
                player.renderEmote("climbing")
                player.walkOverDelay(target.tile.addX(1))
                player.exp(Skill.Agility, 8.0)
                player.clearRenderEmote()
            }
        } else if (target.tile.equals(2888, 3661)) {
            if (player.tile.y >= target.tile.y) {
                player.anim("rocks_climb_down", delay = 15)
                player.exactMoveDelay(target.tile.addY(-1), 50, direction = Direction.NORTH)
                player.exp(Skill.Agility, 1.0)
                player.clearAnim()
            } else {
                player.renderEmote("climbing")
                player.walkOverDelay(target.tile.addY(1))
                player.exp(Skill.Agility, 1.0)
                player.clearRenderEmote()
            }
        }
    }

    @Option("Climb", "trollheim_rocks_hard")
    suspend fun climbHardRocks(player: Player, target: GameObject) {
        player.face(target)
        if (target.tile.equals(2908, 3682)) {
            player.anim("human_climbing_down", delay = 10)
            player.exactMoveDelay(target.tile, 40, direction = Direction.WEST)
            player.anim("human_climbing_down", delay = 10)
            player.exactMoveDelay(Tile(2909, 3684), 120, direction = Direction.SOUTH)
            player.exp(Skill.Agility, 8.0)
            player.face(Direction.SOUTH)
            player.clearAnim()
        } else if (target.tile.equals(2909, 3683)) {
            player.renderEmote("climbing")
            player.walkOverDelay(Tile(2909, 3682))
            player.clearRenderEmote()
            player.delay()
            player.renderEmote("climbing")
            player.walkOverDelay(Tile(2907, 3682))
            player.exp(Skill.Agility, 8.0)
            player.clearRenderEmote()
        }
    }

    @Option("Climb", "trollheim_rocks_advanced")
    suspend fun climbAdvancedRocks(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 44)) {
            return
        }
        player.face(target)
        player.message("You climb onto the rock...")
        player.anim("rocks_pile_climb", delay = 30)
        val direction = if (player.tile.x >= target.tile.x) Direction.WEST else Direction.EAST
        player.exactMoveDelay(target.tile.add(direction), startDelay = 30, delay = 94, direction = direction)
        player.message("...and step down the other side.")
        player.clearAnim()
    }

    @Option("Climb", "trollheim_rocks_medium")
    suspend fun climbMediumRocks(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 43)) {
            return
        }
        // TODO 1-4 damage on failure
        player.face(Direction.NORTH)
        if (player.tile.y >= target.tile.y) {
            player.anim("human_climbing_down", delay = 10)
            player.exactMoveDelay(target.tile.addY(-2), 120, direction = Direction.NORTH)
            player.clearAnim()
        } else {
            player.renderEmote("climbing")
            player.walkOverDelay(target.tile.addY(2))
            player.clearRenderEmote()
        }
        player.exp(Skill.Agility, 8.0)
    }

    @Option("Climb", "trollheim_rocks_easy")
    suspend fun climbEasyRocks(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 41)) {
            return
        }
        // TODO 1-4 damage on failure
        player.face(Direction.NORTH)
        if (player.tile.x >= target.tile.x) {
            player.anim("human_climbing_down", delay = 10)
            player.exactMoveDelay(target.tile.addX(-2), 120, direction = Direction.EAST)
            player.clearAnim()
        } else {
            player.renderEmote("climbing")
            player.walkOverDelay(target.tile.addX(2))
            player.clearRenderEmote()
        }
        player.exp(Skill.Agility, 8.0)
    }

    // TODO levels and failing
}

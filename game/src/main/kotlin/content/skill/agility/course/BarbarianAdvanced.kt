package content.skill.agility.course

import world.gregs.voidps.engine.entity.character.move.tele
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
import world.gregs.voidps.type.sub.Option

class BarbarianAdvanced {

    @Option("Run-up", "barbarian_outpost_run_wall")
    suspend fun runUp(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 90, message = true)) {
            return
        }
        player.clear("face_entity")
        player.delay()
        player.face(Direction.NORTH)
        player.anim("barbarian_wall_jump_climb")
        player.delay(7)
        player.anim("barbarian_wall_jump")
        player.exactMoveDelay(Tile(2538, 3545, 2), 30, Direction.NORTH)
        player.delay(1)
        player.exp(Skill.Agility, 15.0)
        player.agilityStage(3)
    }

    @Option("Climb-up", "barbarian_outpost_climb_wall")
    suspend fun climbWall(player: Player, target: GameObject) {
        player.clear("face_entity")
        player.walkToDelay(Tile(2537, 3546, 2))
        player.face(Direction.WEST)
        player.delay()
        player.anim("barbarian_wall_climb")
        player.delay()
        player.tele(2536, 3546, 3)
        player.anim("barbarian_wall_stand_up")
        player.delay()
        player.exp(Skill.Agility, 15.0)
        player.agilityStage(4)
    }

    @Option("Fire", "barbarian_outpost_spring")
    suspend fun fireSpring(player: Player, target: GameObject) {
        player.clear("face_entity")
        player.face(Direction.NORTH)
        player.delay(1)
        target.anim("barbarian_spring_fire")
        player.delay(1)
        player.tele(2533, 3547, 3)
        player.anim("barbarian_spring_shoot")
        player.exactMoveDelay(Tile(2532, 3553, 3), 60, Direction.NORTH)
        target.anim("barbarian_spring_reset")
        player.delay(2)
        player.exp(Skill.Agility, 15.0)
        player.agilityStage(5)
    }

    @Option("Cross", "barbarian_outpost_balance_beam")
    suspend fun crossBeam(player: Player, target: GameObject) {
        player.face(Direction.EAST)
        player.delay()
        player.anim("circus_cartwheel")
        player.delay()
        player.exactMoveDelay(Tile(2536, 3553, 3), 45, Direction.EAST)
        player.renderEmote("beam_balance")
        player.delay()
        player.exp(Skill.Agility, 15.0)
        player.agilityStage(6)
    }

    @Option("Jump-over", "barbarian_outpost_gap")
    suspend fun jumpGap(player: Player, target: GameObject) {
        player.clearRenderEmote()
        player.anim("jump_down")
        player.delay()
        player.tele(2539, 3553, 2)
        player.anim("jump_land")
        player.delay()
        player.exp(Skill.Agility, 15.0)
        player.agilityStage(7)
    }

    @Option("Slide-down", "barbarian_outpost_roof")
    suspend fun slideDownRoof(player: Player, target: GameObject) {
        player.anim("barbarian_slide_start")
        player.exactMoveDelay(player.tile.copy(x = 2540), 30, Direction.EAST)
        player.anim("barbarian_slide")
        player.exactMove(player.tile.copy(x = 2543, level = 1), 90, Direction.EAST)
        player.delay()
        player.anim("barbarian_slide")
        player.delay()
        player.anim("barbarian_slide_jump")
        player.delay()
        player.tele(2544, player.tile.y, 0)
        player.anim("jump_land")
        player.delay()
        player.exp(Skill.Agility, 15.0)
        if (player.agilityStage == 7) {
            player.agilityStage = 0
            player.inc("barbarian_course_advanced_laps")
            player.exp(Skill.Agility, 615.0)
        }
    }

}

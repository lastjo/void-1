package content.skill.agility.course

import content.entity.combat.hit.damage
import content.entity.obj.door.enterDoor
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Level
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.equals
import world.gregs.voidps.type.sub.Option

class WildernessCourse(private val objects: GameObjects) {

    @Option("Open", "wilderness_agility_door_closed")
    suspend fun openDoor(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 52, message = true)) {
            // TODO proper message
            return
        }
        if (player.tile.y > 3916) {
            player.enterDoor(target)
            player.clearRenderEmote()
            return
        }
        // Not sure if you can fail going up
        //    val disable = Settings["agility.disableCourseFailure", false]
        val success = true // disable || Level.success(player.levels.get(Skill.Agility), 200..250)
        player.message("You go through the gate and try to edge over the ridge...", ChatType.Filter)
        player.enterDoor(target, delay = 1)
        player.renderEmote("beam_balance")
        //    if (!success) {
        //        fallIntoPit(player)
        //        return@strongQueue
        //    }
        player.walkOverDelay(Tile(2998, 3930))
        player.clearRenderEmote()
        val gateTile = Tile(2998, 3931)
        val gate = objects[gateTile, "wilderness_agility_gate_east_closed"]
        if (gate != null) {
            player.enterDoor(gate)
        } else {
            player.walkOverDelay(gateTile)
        }
        player.message("You skillfully balance across the ridge...", ChatType.Filter)
        player.exp(Skill.Agility, 15.0)
        player.agilityCourse("wilderness")
    }

    @Option("Open", "wilderness_agility_gate_east_closed", "wilderness_agility_gate_west_closed")
    suspend fun openGate(player: Player, target: GameObject) {
        if (player.tile.y < 3931) {
            player.enterDoor(target, delay = 2)
            player.clearRenderEmote()
            return
        }
        val disable = Settings["agility.disableCourseFailure", false]
        val success = disable || Level.success(player.levels.get(Skill.Agility), 200..250)
        player.message("You go through the gate and try to edge over the ridge...", ChatType.Filter)
        player.walkToDelay(player.tile.copy(x = player.tile.x.coerceIn(2997, 2998)))
        player.enterDoor(target)
        player.renderEmote("beam_balance")
        if (!success) {
            fallIntoPit(player)
            return
        }
        player.walkOverDelay(Tile(2998, 3917))
        player.clearRenderEmote()
        val door = objects[Tile(2998, 3917), "wilderness_agility_door_closed"]
        if (door != null) {
            player.enterDoor(door, delay = 1)
        } else {
            player.walkOverDelay(Tile(2998, 3916))
        }
        player.message("You skillfully balance across the ridge...", ChatType.Filter)
        player.exp(Skill.Agility, 15.0)
    }

    @Option("Squeeze-through", "wilderness_obstacle_pipe")
    suspend fun squeezeThroughPipe(player: Player, target: GameObject) {
        if (!target.tile.equals(3004, 3938)) {
            player.message("You can't enter the pipe from this side.")
            return
        }
        if (player.tile.y == 3938) {
            player.walkToDelay(target.tile.addY(-1))
        }
        player.anim("climb_through_pipe", delay = 30)
        player.exactMoveDelay(Tile(3004, 3940), startDelay = 30, delay = 96, direction = Direction.NORTH)
        player.tele(3004, 3947)
        player.delay()
        player.anim("climb_through_pipe", delay = 30)
        player.exactMoveDelay(Tile(3004, 3950), startDelay = 30, delay = 96, direction = Direction.NORTH)
        player.exp(Skill.Agility, 12.5)
        player.agilityStage(1)
    }

    @Option("Swing-on", "wilderness_rope_swing")
    suspend fun swingOnRope(player: Player, target: GameObject) {
        player.walkToDelay(target.tile.copy(y = 3953))
        player.clear("face_entity")
        player.face(Direction.NORTH)
        val disable = Settings["agility.disableCourseFailure", false]
        val success = disable || Level.success(player.levels.get(Skill.Agility), 200..250)
        player.anim("rope_swing")
        target.anim("swing_rope")
        player.delay()
        if (success) {
            player.exactMoveDelay(player.tile.copy(y = 3958), 60, Direction.NORTH)
            player.exp(Skill.Agility, 20.0)
            player.message("You skillfully swing across.", ChatType.Filter)
        } else {
            player.exactMoveDelay(player.tile.copy(y = 3957), 50, Direction.NORTH)
            player.delay(1)
            player.tele(3004, 10357)
            player.damage((player.levels.get(Skill.Constitution) * 0.15).toInt() + 10)
            player.message("You slip and fall to the pit below.", ChatType.Filter)
        }
        if (success || Settings["agility.disableFailLapSkip", false]) {
            player.agilityStage(2)
        }
    }

    @Option("Cross", "wilderness_stepping_stone")
    suspend fun crossStones(player: Player, target: GameObject) {
        player.message("You carefully start crossing the stepping stones...", ChatType.Filter)
        for (i in 0..5) {
            player.anim("stepping_stone_jump")
            player.sound("jump")
            player.exactMoveDelay(target.tile.addX(-i), delay = 30, direction = Direction.WEST, startDelay = 15)
            player.delay(1)
            if (i == 2 && !Settings["agility.disableCourseFailure", false] && !Level.success(player.levels.get(Skill.Agility), 180..250)) {
                player.anim("rope_walk_fall_down")
                player.face(Direction.WEST)
                player.clearRenderEmote()
                player.message("...You lose your footing and fall into the lava.", ChatType.Filter)
                player.delay(2)
                player.damage(player.levels.get(Skill.Constitution) / 5 + 10)
                player.tele(3002, 3963)
                if (Settings["agility.disableFailLapSkip", false]) {
                    player.agilityStage(3)
                }
                return
            }
        }
        player.message("...You safely cross to the other side.", ChatType.Filter)
        player.exp(Skill.Agility, 20.0)
        player.agilityStage(3)
    }

    @Option("Walk-across", "wilderness_log_balance")
    suspend fun walkAcross(player: Player, target: GameObject) {
        player.message("You walk carefully across the slippery log...", ChatType.Filter)
        val disable = Settings["agility.disableCourseFailure", false]
        val success = disable || Level.success(player.levels.get(Skill.Agility), 200..250)
        if (success) {
            player.walkOverDelay(target.tile)
            player.renderEmote("beam_balance")
            player.walkOverDelay(Tile(2994, 3945))
            player.message("You skillfully edge across the gap.", type = ChatType.Filter)
            player.clearRenderEmote()
            player.delay()
            player.exp(Skill.Agility, 20.0)
            player.agilityStage(4)
        } else {
            player.walkOverDelay(target.tile)
            player.renderEmote("beam_balance")
            player.walkOverDelay(Tile(2998, 3945))
            player.message("You slip and fall onto the spikes below.", type = ChatType.Filter)
            player.anim("rope_walk_fall_down")
            player.face(Direction.NORTH)
            player.delay()
            player.tele(2998, 10346)
            player.clearRenderEmote()
            player.sound("2h_stab")
            player.delay()
            player.walkOverDelay(Tile(2998, 10345))
            player.damage((player.levels.get(Skill.Constitution) * 0.15).toInt() + 10)
            player.sound("male_defend_1", delay = 20)
        }
        if (success || Settings["agility.disableFailLapSkip", false]) {
            player.agilityStage(4)
        }
    }

    @Option("Climb", "wilderness_agility_rocks")
    suspend fun climbRocks(player: Player, target: GameObject) {
        player.message("You walk carefully across the slippery log...", ChatType.Filter)
        player.renderEmote("climbing")
        player.walkOverDelay(player.tile.copy(y = 3933))
        player.clearRenderEmote()
        player.message("You reach the top.", type = ChatType.Filter)
        if (player.agilityStage == 4) {
            player.agilityStage = 0
            player.exp(Skill.Agility, 499.0)
            player.inc("wilderness_course_laps")
        }
    }

    suspend fun fallIntoPit(player: Player) {
        player.walkOverDelay(Tile(2998, 3924))
        player.clearRenderEmote()
        player.face(Direction.NORTH)
        player.anim("rope_walk_fall_down")
        player.message("You lose your footing and fall into the wolf pit.", ChatType.Filter)
        player.delay()
        player.exactMoveDelay(Tile(3001, 3923), 25, Direction.SOUTH)
    }
}

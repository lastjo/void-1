package content.area.misthalin.edgeville

import content.entity.sound.sound
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
import world.gregs.voidps.type.sub.Option

class EdgevilleMonkeyBars {

    @Option("Swing across", "edgeville_monkey_bars")
    suspend fun operate(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 15)) {
            player.message("You need at least 15 Agility to do that.") // TODO proper message
            return
        }
        val north = player.tile.y > 9967
        val y = if (north) 9964 else 9969
        val x = if (target.tile.x == 3119) 3120 else 3121
        player.walkToDelay(Tile(x, target.tile.y))
        player.clear("face_entity")
        player.face(if (north) Direction.SOUTH else Direction.NORTH)
        player.delay()
        player.sound("monkeybars_on")
        player.anim("jump_onto_monkey_bars")
        player.renderEmote("monkey_bars")
        player.delay(2)
        player.sound("monkeybars_loop", repeat = 11)
        player.walkOverDelay(Tile(x, y), forceWalk = true)
        player.delay()
        player.clearRenderEmote()
        player.anim("jump_from_monkey_bars")
        player.sound("monkeybars_off")
        player.exp(Skill.Agility, 20.0)
    }

}

package content.area.kharidian_desert.al_kharid

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option

class AlKharidMine {

    @Option("Climb", "al_kharid_mine_shortcut_bottom")
    suspend fun climbUpShortcut(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 38)) {
            player.message("You must have an Agility level of at least 38 to climb these rocks.") // TODO proper message
            return
        }
        player.face(Direction.EAST)
        player.delay()
        player.walkToDelay(Tile(3303, 3315))
        player.renderEmote("climbing")
        player.walkOverDelay(Tile(3307, 3315))
        player.clearRenderEmote()
    }

    @Option("Climb", "al_kharid_mine_shortcut_top")
    suspend fun climbDownShortcut(player: Player, target: GameObject) {
        if (!player.has(Skill.Agility, 38)) {
            player.message("You must have an Agility level of at least 38 to climb these rocks.") // TODO proper message
            return
        }
        player.face(Direction.EAST)
        player.delay()
        player.walkOverDelay(Tile(3305, 3315))
        player.face(Direction.WEST)
        player.delay()
        player.anim("human_climbing_down", delay = 10)
        player.exactMoveDelay(Tile(3303, 3315), delay = 120, direction = Direction.EAST)
    }

}

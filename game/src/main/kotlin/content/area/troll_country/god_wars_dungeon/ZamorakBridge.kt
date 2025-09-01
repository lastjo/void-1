package content.area.troll_country.god_wars_dungeon

import content.entity.gfx.areaGfx
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.clearRenderEmote
import world.gregs.voidps.engine.entity.character.player.renderEmote
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class ZamorakBridge {

    @Option("Climb-off", "godwars_zamorak_bridge")
    suspend fun operate(player: Player, target: GameObject) {
        if (!player.has(Skill.Constitution, 700, message = true)) {
            return
        }
        val direction = if (player.tile.y <= target.tile.y) Direction.NORTH else Direction.SOUTH
        player["godwars_darkness"] = direction == Direction.NORTH
        player.face(direction)
        player.walkToDelay(target.tile)
        player.delay()
        player.tele(target.tile.addY(direction.delta.y * 2))
        player.renderEmote("swim")
        areaGfx("big_splash", player.tile)
        player.delay(4)
        player.message("Dripping, you climb out of the water.")
        if (direction == Direction.NORTH) {
            player.levels.set(Skill.Prayer, 0)
            player.message("The extreme evil of this area leaves your Prayer drained.")
        }
        player.tele(target.tile.addY(direction.delta.y * 12))
        player.clearRenderEmote()
    }

}

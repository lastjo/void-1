package content.quest.member.ghosts_ahoy

import content.entity.obj.ObjectTeleports
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.queue.strongQueue
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.sub.Teleport

class Ectopool(private val teleports: ObjectTeleports) {

    @Teleport("Jump-down", "ectopool_shortcut_rail")
    fun jump(player: Player, target: GameObject): Int {
        if (!player.has(Skill.Agility, 58)) {
            player.message("You need an agility level of at least 58 to climb down this wall.")
            return -1
        }
        player.anim("jump_down")
        return 1
    }

    @Teleport("Jump-down", "ectopool_shortcut_rail")
    fun land(player: Player, target: GameObject) {
        player.anim("jump_land")
    }

    @Teleport("Jump-up", "ectopool_shortcut_wall")
    fun shortcut(player: Player, target: GameObject, def: ObjectDefinition, option: String): Int {
        if (!player.has(Skill.Agility, 58)) {
            player.message("You need an agility level of at least 58 to climb up this wall.")
            return -1
        }
        player.strongQueue("teleport") {
            val teleport = teleports.get(option)[target.tile.id]!!
            val tile = ObjectTeleports.calculate(teleport, player)
            player.tele(tile.addX(1))
            player.exactMoveDelay(tile, startDelay = 49, delay = 68, direction = Direction.WEST)
            player.anim("jump_up")
            Publishers.all.teleportLandGameObject(player, target, def, option)
        }
        return -1
    }

}

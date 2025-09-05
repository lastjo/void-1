package content.skill.magic.spell

import content.entity.sound.sound
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.*
import world.gregs.voidps.engine.get
import world.gregs.voidps.engine.map.collision.random
import world.gregs.voidps.engine.queue.strongQueue
import world.gregs.voidps.type.Tile

class Teleport {
    companion object {
        fun teleport(player: Player, area: String, type: String) {
            teleport(player, get<AreaDefinitions>()[area].random(player)!!, type)
        }

        fun teleport(player: Player, tile: Tile, type: String, sound: Boolean = true) {
            if (player.queue.contains("teleport")) {
                return
            }
            player.closeInterfaces()
            player.strongQueue("teleport", onCancel = null) {
                val teleport = Publishers.all.teleport(player, type)
                if (teleport == -1) {
                    return@strongQueue
                }
                player.steps.clear()
                if (sound) {
                    player.sound("teleport")
                }
                player.gfx("teleport_$type")
                player.animDelay("teleport_$type")
                player.tele(tile)
                delay(1)
                if (sound) {
                    player.sound("teleport_land")
                }
                player.gfx("teleport_land_$type")
                player.animDelay("teleport_land_$type")
                if (teleport > 0) {
                    delay(teleport)
                    player.clearAnim()
                }
                Publishers.all.teleportLand(player, type)
            }
        }
    }
}

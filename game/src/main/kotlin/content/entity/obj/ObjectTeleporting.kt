package content.entity.obj

import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Teleport

class ObjectTeleporting(private val teleports: ObjectTeleports) {

    @Option
    suspend fun teleport(player: Player, target: GameObject, def: ObjectDefinition, option: String) {
        val teleport = teleports.get(option)
        if (teleport.isEmpty()) {
            return
        }
        player.delay()
        teleports.teleport(player, target, def, option)
    }

}

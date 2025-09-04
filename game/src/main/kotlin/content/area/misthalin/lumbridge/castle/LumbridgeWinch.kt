package content.area.misthalin.lumbridge.castle

import content.entity.sound.areaSound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Option

class LumbridgeWinch {

    @Option("Operate", "lumbridge_winch")
    suspend fun operate(player: Player, target: GameObject) {
        player.message("It seems the winch is jammed. You can't move it.")
        areaSound("lever", target.tile)
    }
}

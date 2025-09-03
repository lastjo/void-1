package content.area.misthalin.lumbridge

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Option

class LumbridgeBookcase {

    @Option("Search", "lumbridge_bookcase")
    suspend fun search(player: Player, target: GameObject) {
        player.message("You search the books...")
        player.delay(2)
        when (random.nextInt(0, 3)) {
            0 -> player.message("None of them look very interesting.")
            1 -> player.message("You find nothing to interest you.")
            2 -> player.message("You don't find anything that you'd ever want to read.")
        }
    }
}

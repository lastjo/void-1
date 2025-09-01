package content.entity.obj

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class Boxes {

    @Option("Search", "lumbridge_boxes")
    fun search(player: Player, target: GameObject) {
        player.message("There is nothing interesting in these boxes.")
    }

}

package content.area.misthalin.lumbridge.roddecks_house

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.Option

class RoddecksBookcase {

    @Option("Search", "roddecks_bookcase")
    suspend fun operate(player: Player, target: GameObject) {
        if (player.inventory.contains("roddecks_diary") && player.inventory.contains("manual_unstable_foundations")) {
            player.message("There's nothing particularly interesting here.")
            return
        }
        if (!player.inventory.contains("roddecks_diary")) {
            player.inventory.add("roddecks_diary")
        }
        if (!player.inventory.contains("manual_unstable_foundations")) {
            player.inventory.add("manual_unstable_foundations")
        }
    }
}

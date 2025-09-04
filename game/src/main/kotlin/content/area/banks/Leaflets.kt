package content.area.banks

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.Option

class Leaflets {

    @Option("Take", "*_bank_leaflet")
    fun take(player: Player, target: GameObject) {
        if (player.inventory.contains("leaflet")) {
            player.message("You already have a copy of the leaflet.")
        } else {
            player.inventory.add("leaflet")
        }
    }
}

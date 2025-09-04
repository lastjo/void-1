package content.area.asgarnia.taverley

import content.entity.obj.door.enterDoor
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.Option

class DustyKey {

    @Option("Open", "gate_63_closed")
    suspend fun operate(player: Player, target: GameObject) {
        if (player.inventory.contains("dusty_key")) {
            player.sound("unlock")
            player.enterDoor(target)
        } else {
            player.sound("locked")
            player.message("The gate is locked.")
        }
    }
}

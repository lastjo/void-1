package content.area.misthalin.varrock

import content.entity.obj.door.enterDoor
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class BrassKey {

    @Option("Open", "edgeville_dungeon_door_closed")
    suspend fun operate(player: Player, target: GameObject) {
        if (player.inventory.contains("brass_key")) {
            player.sound("unlock")
            player.enterDoor(target)
        } else {
            player.sound("locked")
            player.message("The door is locked. You need a brass key to open it.")
        }
    }

}

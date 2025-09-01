package content.entity.obj

import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.data.Pickable
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.entity.obj.remove
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Option
import java.util.concurrent.TimeUnit

class Picking {

    @Option("Pick")
    fun pick(player: Player, target: GameObject) {
        val pickable: Pickable = target.def.getOrNull("pickable") ?: return
        if (player.inventory.add(pickable.item)) {
            player.sound("pick")
            player.anim("climb_down")
            if (random.nextInt(pickable.chance) == 0) {
                target.remove(TimeUnit.SECONDS.toTicks(pickable.respawnDelay))
            }
            player.message(pickable.message)
        } else {
            player.inventoryFull()
        }
    }

}

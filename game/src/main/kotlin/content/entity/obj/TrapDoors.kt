package content.entity.obj

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option
import java.util.concurrent.TimeUnit

class TrapDoors {

    @Option("Open", "trapdoor_*_closed")
    fun open(player: Player, target: GameObject) {
        player.anim("open_chest")
        target.replace(target.id.replace("_closed", "_opened"), ticks = TimeUnit.MINUTES.toTicks(3))
    }

    @Option("Close", "trapdoor_*_opened")
    fun close(player: Player, target: GameObject) {
        player.anim("close_chest")
        target.replace(target.id.replace("_opened", "_closed"), ticks = TimeUnit.MINUTES.toTicks(3))
    }

}

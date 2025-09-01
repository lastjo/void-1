package content.entity.obj

import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class BushPicking {

    @Option("Pick-from", "cadava_bush_full", "cadava_bush_half")
    fun cadava(player: Player, target: GameObject) {
        if (!player.inventory.add("cadava_berries")) {
            player.message("Your inventory is too full to pick the berries from the bush.")
            return
        }
        player.sound("pick")
        player.anim("pick_plant")
        target.replace(if (target.id == "cadava_bush_full") "cadava_bush_half" else "cadava_bush_empty", ticks = Settings["world.objs.cadava.regrowTicks", 200])
    }

    @Option("Pick-from", "redberry_bush_full", "redberry_bush_half")
    fun redberry(player: Player, target: GameObject) {
        if (!player.inventory.add("redberries")) {
            player.message("Your inventory is too full to pick the berries from the bush.")
            return
        }
        player.sound("pick")
        player.anim("pick_plant")
        target.replace(if (target.id == "redberry_bush_full") "redberry_bush_half" else "redberry_bush_empty", ticks = Settings["world.objs.redberry.regrowTicks", 200])
    }

    @Option("Pick-from", "cadava_bush_empty", "redberry_bush_empty")
    fun empty(player: Player, target: GameObject) {
        player.message("There are no berries on this bush at the moment.")
    }

}

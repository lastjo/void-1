package content.area.misthalin.varrock.palace

import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.type.sub.Option

class VarrockManhole {

    @Option("Open", "varrock_manhole")
    fun open(player: Player, target: GameObject) {
        target.replace("varrock_manhole_open")
        player.message("You pull back the cover from over the manhole.")
        player.sound("coffin_open")
    }

    @Option("Close", "varrock_manhole")
    fun close(player: Player, target: GameObject) {
        target.replace("varrock_manhole")
        player.message("You place the cover back over the manhole.")
    }
}

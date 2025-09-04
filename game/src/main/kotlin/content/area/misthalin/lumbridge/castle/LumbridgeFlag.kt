package content.area.misthalin.lumbridge.castle

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Option

class LumbridgeFlag {

    @Option("Raise", "lumbridge_flag")
    suspend fun raise(player: Player, target: GameObject) {
        target.anim("lumbridge_flag")
        player.animDelay("lumbridge_flag_raise")
        player.animDelay("lumbridge_flag_stop_raise")
        player.say("All Hail the Duke!")
        player.animDelay("emote_salute")
        player["raise_the_roof_task"] = true
    }
}

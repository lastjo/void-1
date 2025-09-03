package content.area.kandarin.ourania

import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class OuraniaCrack {

    @Option("Squeeze-through", "ourania_crack_enter")
    suspend fun enter(player: Player, target: GameObject) {
        player.open("fade_out")
        player.delay(3)
        player.tele(3312, 4817)
        player.delay(1)
        player.open("fade_in")
    }

    @Option("Squeeze-through", "ourania_crack_exit")
    suspend fun exit(player: Player, target: GameObject) {
        player.open("fade_out")
        player.delay(3)
        player.tele(3308, 4819)
        player.delay(1)
        player.open("fade_in")
    }
}

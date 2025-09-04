package content.entity.npc.shop

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers

fun Player.openShop(id: String) {
    Publishers.all.publishPlayer(this, "open_shop", id)
}

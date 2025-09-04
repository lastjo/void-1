package content.skill.crafting

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.type.sub.Subscribe

class SoftClay {

    @Subscribe("crafted", "soft_clay")
    fun use(player: Player) {
        player.message("You now have some soft, workable clay.", ChatType.Filter)
    }
}

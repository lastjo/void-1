package content.skill.crafting

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Subscribe

class Thread {

    @Subscribe("craft_required", "thread")
    fun used(player: Player) {
        val value = player.inc("thread_used")
        if (value == 5) {
            player.clear("thread_used")
            player.inventory.remove("thread")
            player.message("You use up one of your reels of thread.")
        }
    }
}

package content.area.misthalin.lumbridge.farm

import content.entity.player.bank.ownsItem
import content.quest.questCompleted
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Take
import java.util.concurrent.TimeUnit

class SethGroatsFarm {

    @Option("Take-hatchet", "hatchet_logs")
    fun takeHatchet(player: Player, target: GameObject) {
        if (player.inventory.add("bronze_hatchet")) {
            target.replace("logs", ticks = TimeUnit.MINUTES.toTicks(3))
        } else {
            player.inventoryFull()
        }
    }

    @Take("super_large_egg")
    fun canTake(player: Player): String {
        if (player.questCompleted("cooks_assistant")) {
            player.message("You've no reason to pick that up; eggs of that size are only useful for royal cakes.")
            return "cancel"
        } else if (player.ownsItem("super_large_egg")) {
            player.message("You've already got one of those eggs and one's enough.")
            return "cancel"
        }
        return ""
    }
}

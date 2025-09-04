package content.skill.cooking

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.sub.UseOn

class Filling {

    @UseOn(on = "sink*")
    @UseOn(on = "fountain*")
    @UseOn(on = "well*")
    @UseOn(on = "water_trough*")
    @UseOn(on = "pump_and_drain*")
    suspend fun use(player: Player, target: GameObject, item: Item) {
        if (!item.def.contains("full")) {
            return
        }
        while (player.inventory.contains(item.id)) {
            player.anim("take")
            player.inventory.replace(item.id, item.def["full"])
            player.delay(if (item.id == "vase") 3 else 1)
            player.message("You fill the ${item.def.name.substringBefore(" (").lowercase()} from the ${target.def.name.lowercase()}", ChatType.Filter)
        }
    }
}

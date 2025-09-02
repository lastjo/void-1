package content.skill.firemaking

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.engine.inv.transact.operation.ReplaceItem.replace
import world.gregs.voidps.type.sub.UseOn

class Firelighters {

    @UseOn("logs", "*firelighter")
    fun use(player: Player, fromItem: Item, toItem: Item) {
        player.inventory.transaction {
            remove(fromItem.id)
            val colour = fromItem.id.removeSuffix("_firelighter")
            replace(toItem.id, "${colour}_logs")
        }
    }

}

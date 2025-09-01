package content.entity.player.inv.item.drop

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.inv.inventoryOption
import content.entity.player.inv.item.tradeable
import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Inventory

class ItemDropping(private val floorItems: FloorItems){

    val logger = InlineLogger()

    @Inventory("Drop")
    fun drop(player: Player, item: Item, itemSlot: Int) {
        player.queue.clearWeak()
        val event = Droppable(item, player.tile)
        player.emit(event)
        if (event.cancelled) {
            return
        }
        if (player.inventory.remove(itemSlot, item.id, item.amount)) {
            if (item.tradeable) {
                floorItems.add(player.tile, item.id, item.amount, revealTicks = 100, disappearTicks = 200, owner = player)
            } else {
                floorItems.add(player.tile, item.id, item.amount, charges = item.charges(), revealTicks = FloorItems.NEVER, disappearTicks = 300, owner = player)
            }
            player.emit(Dropped(item))
            player.sound("drop_item")
        } else {
            logger.info { "Error dropping item $item for $player" }
        }
    }

}

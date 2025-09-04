package content.entity.player.inv.item.destroy

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.dialogue.type.destroy
import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Inventory

class ItemDestroy {

    val logger = InlineLogger()

    @Inventory("Destroy")
    @Inventory("Dismiss")
    @Inventory("Release")
    suspend fun destroy(player: Player, item: Item, itemSlot: Int, option: String) = player.dialogue {
        if (item.isEmpty() || item.amount <= 0) {
            logger.info { "Error destroying item $item for $player" }
            return@dialogue
        }
        val message = item.def[
            "destroy",
            """
                Are you sure you want to ${option.lowercase()} ${item.def.name}?
                You won't be able to reclaim it.
            """,
        ]
        val destroy = destroy(item.id, message)
        if (!destroy) {
            return@dialogue
        }
        if (Publishers.all.publishPlayer(player, "can_destroy", item.id)) {
            return@dialogue
        }
        if (player.inventory.remove(itemSlot, item.id, item.amount)) {
            player.sound("destroy_object")
            Publishers.all.publishPlayer(player, "destroyed", item.id)
            logger.info { "$player destroyed item $item" }
        } else {
            logger.info { "Error destroying item $item for $player" }
        }
    }
}

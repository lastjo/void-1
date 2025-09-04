package content.entity.player.inv.item.take

import com.github.michaelbull.logging.InlineLogger
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.SetCharge.setCharge
import world.gregs.voidps.type.sub.Option

class ItemTake(private val floorItems: FloorItems) {

    val logger = InlineLogger()

    @Option("Take")
    suspend fun operate(player: Player, target: FloorItem) {
        player.approachRange(-1)
        if (player.inventory.isFull() && (!player.inventory.stackable(target.id) || !player.inventory.contains(target.id))) {
            player.inventoryFull()
            return
        }
        var item = Publishers.all.playerTakeItem(player, target.id)
        if (item == "null" || item == "cancel") {
            return
        }
        if (item.isBlank()) {
            item = target.id
        }
        if (!floorItems.remove(target)) {
            player.message("Too late - it's gone!")
            return
        }
        player.inventory.transaction {
            val freeIndex = inventory.freeIndex()
            add(item, target.amount)
            if (target.charges > 0) {
                setCharge(freeIndex, target.charges)
            }
        }
        when (player.inventory.transaction.error) {
            TransactionError.None -> {
                if (player.tile != target.tile) {
                    player.face(target.tile.delta(player.tile))
                    player.anim("take")
                }
                player.sound("take_item")
            }
            is TransactionError.Full -> player.inventoryFull()
            else -> logger.warn { "Error taking item $target ${player.inventory.transaction.error}" }
        }
    }

    @Option("Take")
    suspend fun operate(npc: NPC, target: FloorItem) {
        if (!floorItems.remove(target)) {
            logger.warn { "$npc unable to take $target." }
        }
        if (npc.id == "ash_cleaner") {
            npc.anim("cleaner_sweeping")
            npc.delay(2)
            npc.clearAnim()
        }
    }
}

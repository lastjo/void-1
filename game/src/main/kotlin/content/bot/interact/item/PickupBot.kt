package content.bot.interact.item

import content.bot.isBot
import kotlinx.coroutines.CancellableContinuation
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.type.sub.Spawn
import kotlin.coroutines.resume

class PickupBot(private val players: Players) {

    @Spawn()
    fun spawn(floorItem: FloorItem) {
        val hash = floorItem.hashCode()
        players.forEach { bot ->
            if (bot.isBot && bot.contains("floor_item_job") && bot["floor_item_hash", -1] == hash) {
                val job: CancellableContinuation<Unit> = bot.remove("floor_item_job") ?: return
                bot.clear("floor_item_hash")
                job.resume(Unit)
            }
        }
    }
}

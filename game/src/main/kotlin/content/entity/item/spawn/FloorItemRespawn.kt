package content.entity.item.spawn

import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.item.floor.ItemSpawns
import world.gregs.voidps.type.sub.Despawn

class FloorItemRespawn(
    private val items: FloorItems,
    private val spawns: ItemSpawns,
) {

    @Despawn
    fun despawn(floorItem: FloorItem) {
        if (isSpawnItem(floorItem)) {
            val spawn = spawns.get(floorItem.tile) ?: return
            items.add(floorItem.tile, spawn.id, spawn.amount, revealTicks = spawn.delay, owner = "")
        }
    }

    fun isSpawnItem(item: FloorItem): Boolean {
        val spawn = spawns.get(item.tile) ?: return false
        return item.id == spawn.id && item.amount == spawn.amount && item.owner == null
    }
}

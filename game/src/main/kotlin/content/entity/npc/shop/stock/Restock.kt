package content.entity.npc.shop.stock

import com.github.michaelbull.logging.InlineLogger
import content.entity.npc.shop.general.GeneralStores
import world.gregs.voidps.cache.config.data.InventoryDefinition
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerDespawn
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.entity.worldSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.timer.*
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max

/**
 * Every [restockTimeTicks] all players shops and [GeneralStores] update their stock by 10%
 */
class Restock(private val inventoryDefinitions: InventoryDefinitions) {

    val restockTimeTicks = TimeUnit.SECONDS.toTicks(60)
    val logger = InlineLogger()

    @Spawn
    fun spawn(player: Player) {
        player.softTimers.restart("shop_restock")
    }

    @TimerStart("shop_restock")
    fun start(player: Player): Int {
        return restockTimeTicks
    }

    @TimerTick("shop_restock")
    fun tick(player: Player) {
        for ((name, inventory) in player.inventories.instances) {
            val def = inventoryDefinitions.get(name)
            if (!def["shop", false]) {
                continue
            }
            restock(def, inventory)
        }
    }

    // Remove restocked shops to save space
    @Despawn
    fun despawn(player: Player) {
        val removal = mutableListOf<String>()
        for ((name, inventory) in player.inventories.instances) {
            val def = inventoryDefinitions.get(name)
            if (!def["shop", false]) {
                continue
            }
            val amounts = def.amounts ?: continue
            if (inventory.items.withIndex().all { (index, item) -> item.amount == amounts.getOrNull(index) }) {
                removal.add(name)
            }
        }
        for (name in removal) {
            player.inventories.instances.remove(name)
        }
    }

    @Spawn
    fun spawn(world: World) {
        World.timers.start("general_store_restock")
    }

    @TimerStart("general_store_restock")
    fun start(world: World): Int {
        return restockTimeTicks
    }

    @TimerTick("general_store_restock")
    fun tick(world: World) {
        logger.debug { "Restocking general stores." }
        for ((key, inventory) in GeneralStores.stores) {
            val def = inventoryDefinitions.get(key)
            restock(def, inventory)
        }
    }

    fun restock(def: InventoryDefinition, inventory: Inventory) {
        for (index in 0 until def.length) {
            val id = def.ids?.getOrNull(index)
            var maximum = def.amounts?.getOrNull(index)
            val item = inventory[index]
            if (id == null || maximum == null) {
                maximum = 0
            }
            if (maximum == item.amount) {
                continue
            }
            val difference = abs(item.amount - maximum)
            val percent = max(1, (difference * 0.1).toInt())
            if (item.amount < maximum) {
                inventory.add(item.id, percent)
            } else {
                inventory.remove(item.id, percent)
            }
        }
    }
}

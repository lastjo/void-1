package content.entity.player

import content.social.trade.exchange.GrandExchange
import kotlinx.coroutines.runBlocking
import world.gregs.voidps.engine.data.SaveQueue
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.data.settingsReload
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.worldDespawn
import world.gregs.voidps.engine.entity.worldSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.Subscribe
import java.util.concurrent.TimeUnit

class AutoSave(
    private val players: Players,
    private val saveQueue: SaveQueue,
    private val exchange: GrandExchange,
) {

    @Spawn
    fun spawn(world: World) {
        autoSave()
    }

    @Despawn
    fun despawn(world: World) = runBlocking {
        saveQueue.direct(players).join()
        exchange.save()
    }

    @Subscribe("settings_reload")
    fun settingsReload() {
        val minutes = Settings["storage.autoSave.minutes", 0]
        if (World.contains("auto_save") && minutes <= 0) {
            World.clearQueue("auto_save")
        } else if (!World.contains("auto_save") && minutes > 0) {
            autoSave()
        }
    }

    fun autoSave() {
        val minutes = Settings["storage.autoSave.minutes", 0]
        if (minutes <= 0) {
            return
        }
        World.queue("auto_save", TimeUnit.MINUTES.toTicks(minutes)) {
            for (player in players) {
                saveQueue.save(player)
            }
            exchange.save()
            autoSave()
        }
    }
}

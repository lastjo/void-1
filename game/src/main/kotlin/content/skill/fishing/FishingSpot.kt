package content.skill.fishing

import org.rsmod.game.pathfinder.collision.CollisionStrategies
import org.rsmod.game.pathfinder.collision.CollisionStrategy
import org.rsmod.game.pathfinder.flag.CollisionFlag
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.npcSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.map.collision.Collisions
import world.gregs.voidps.engine.timer.*
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick

class FishingSpot {

    val areas: AreaDefinitions by inject()
    val players: Players by inject()
    val collisions: Collisions by inject()

    val water = CollisionStrategies.Blocked
    val land = CollisionStrategies.Normal

    @Spawn("fishing_spot_*")
    fun spawn(npc: NPC) {
        npc.softTimers.start("fishing_spot_respawn")
    }

    @TimerStart("fishing_spot_respawn")
    fun start(npc: NPC): Int {
        // https://x.com/JagexAsh/status/1604892218380021761
        return random.nextInt(280, 530)
    }

    @TimerTick("fishing_spot_respawn")
    fun tick(npc: NPC): Int {
        move(npc)
        return random.nextInt(280, 530)
    }

    fun move(npc: NPC) {
        val area = areas.get(npc.tile.zone).firstOrNull { it.name.endsWith("fishing_area") } ?: return
        /*
            Find all water tiles that have two water tiles next to them and land perpendicular
               [W]    [L]    [W]
            [L][W] [W][W][W] [W][L] [W][W][W]
               [W]           [W]       [L]
         */
        val tile = area.area.toList().filter { tile ->
            check(tile, water) &&
                (
                    (check(tile.addY(1), water) && check(tile.addY(-1), water) && (check(tile.addX(-1), land) || check(tile.addX(1), land))) ||
                        (check(tile.addX(-1), water) && check(tile.addX(1), water) && (check(tile.addY(1), land) || check(tile.addY(-1), land)))
                    )
        }.randomOrNull() ?: return
        npc.tele(tile)
        npc.softTimers.start("fishing_spot_respawn")
        val fishers: MutableSet<String> = npc.remove("fishers") ?: return
        for (fisher in fishers) {
            val player = players.get(fisher) ?: continue
            player.mode = EmptyMode
            player.queue.clearWeak()
        }
        fishers.clear()
    }

    fun check(tile: Tile, strategy: CollisionStrategy): Boolean {
        val tileFlag = collisions[tile.x, tile.y, tile.level]
        return strategy.canMove(
            tileFlag,
            CollisionFlag.BLOCK_NORTH_AND_SOUTH_EAST or
                CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST or
                CollisionFlag.BLOCK_NPCS,
        )
    }
}

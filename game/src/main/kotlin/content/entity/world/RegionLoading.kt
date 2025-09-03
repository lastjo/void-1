package content.entity.world

import content.bot.isBot
import world.gregs.voidps.engine.client.instruction.InstructionHandlers
import world.gregs.voidps.engine.client.instruction.instruction
import world.gregs.voidps.engine.client.update.view.Viewport
import world.gregs.voidps.engine.entity.MAX_PLAYERS
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.get
import world.gregs.voidps.engine.map.zone.DynamicZones
import world.gregs.voidps.network.client.instruction.FinishRegionLoad
import world.gregs.voidps.network.login.protocol.encode.dynamicMapRegion
import world.gregs.voidps.network.login.protocol.encode.mapRegion
import world.gregs.voidps.type.Distance
import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Instruction
import world.gregs.voidps.type.sub.Move
import world.gregs.voidps.type.sub.Subscribe


/**
 * Keeps track of when players enter and move between regions
 * Loads maps when they are accessed
 */
class RegionLoading(
    private val players: Players,
    private val dynamicZones: DynamicZones,
) {
    val playerRegions = IntArray(MAX_PLAYERS - 1)

    private val blankXtea = IntArray(4)

    init {
        instruction<FinishRegionLoad> { player ->
            if (player["debug", false]) {
                println("Finished region load. $player ${player.viewport}")
            }
            player.viewport?.loaded = true
        }
    }

    @Instruction
    fun regionLoad(player: Player, instruction: FinishRegionLoad) {

    }

    @Subscribe("region_load")
    fun load(player: Player) {
        player.viewport?.seen(player)
        playerRegions[player.index - 1] = player.tile.regionLevel.id
        val viewport = player.viewport ?: return
        players.forEach { other ->
            viewport.seen(other)
        }
        updateRegion(player, true, crossedDynamicBoarder(player))
        viewport.players.addSelf(player)
    }

    /**
     * Resend region load when FinishRegionLoad wasn't received
     */
    @Subscribe("region_retry")
    fun retry(player: Player) {
        if (player.networked) {
            println("Failed to load region. Retrying...")
            updateRegion(player, initial = false, force = true)
        }
    }

    /*
        Player regions
     */

    @Despawn
    fun despawn(player: Player) {
        playerRegions[player.index - 1] = 0
    }

    /*
        Region updating
     */

    @Move
    fun move(player: Player, from: Tile, to: Tile) {
        if (from.regionLevel != to.regionLevel) {
            playerRegions[player.index - 1] = to.regionLevel.id
        }
    }

    @Subscribe("reload_region")
    fun reload(player: Player) {
        if (player.networked && needsRegionChange(player)) {
            updateRegion(player, false, crossedDynamicBoarder(player))
        }
    }

    @Subscribe("reload_zone")
    fun reloadZone(zone: Any) {
        zone as Zone
        players.forEach { player ->
            if (player.networked && inViewOfZone(player, zone)) {
                updateRegion(player, initial = false, force = true)
            }
        }
    }

    @Subscribe("clear_region")
    fun clearRegion(region: Any) {
        region as Region
        players.forEach { player ->
            if (player.networked && inViewOfRegion(player, region)) {
                updateRegion(player, initial = false, force = true)
            }
        }
    }

    fun needsRegionChange(player: Player) = !inViewOfZone(player, player.viewport!!.lastLoadZone) || crossedDynamicBoarder(player)

    fun inViewOfZone(player: Player, zone: Zone): Boolean {
        val viewport = player.viewport!!
        val radius: Int = viewport.zoneRadius - 2
        return Distance.within(player.tile.zone.x, player.tile.zone.y, zone.x, zone.y, radius)
    }

    fun inViewOfRegion(player: Player, region: Region): Boolean {
        val viewport = player.viewport!!
        val radius: Int = viewport.tileSize shr 6
        return Distance.within(player.tile.region.x, player.tile.region.y, region.x, region.y, radius)
    }

    fun crossedDynamicBoarder(player: Player) = player.viewport!!.dynamic != inDynamicView(player)

    fun inDynamicView(player: Player): Boolean = dynamicZones.isDynamic(player.tile.region)

    fun updateRegion(player: Player, initial: Boolean, force: Boolean) {
        val dynamic = inDynamicView(player)
        val viewport = player.viewport!!
        val wasDynamic = viewport.dynamic
        if (dynamic) {
            updateDynamic(player, initial, force)
        } else {
            update(player, initial, force)
        }
        if ((dynamic || wasDynamic) && !initial) {
            viewport.npcs.clear()
        }
        if (!player.isBot) {
            viewport.loaded = false
        }
        viewport.lastLoadZone = player.tile.zone
    }

    fun update(player: Player, initial: Boolean, force: Boolean) {
        val viewport = player.viewport ?: return
        val xteaList = mutableListOf<IntArray>()

        val zone = player.tile.zone
        val zoneX = zone.x
        val zoneY = zone.y

        val radius = viewport.zoneRadius
        for (regionX in (zone.x - radius) / 8..(zone.x + radius) / 8) {
            for (regionY in (zone.y - radius) / 8..(zone.y + radius) / 8) {
                val xtea = blankXtea
                xteaList.add(xtea)
            }
        }

        viewport.dynamic = false

        player.client?.mapRegion(
            zoneX = zoneX,
            zoneY = zoneY,
            forceRefresh = force,
            mapSize = Viewport.VIEWPORT_SIZES.indexOf(viewport.tileSize),
            xteas = xteaList.toTypedArray(),
            clientIndex = if (initial) player.index - 1 else null,
            playerRegions = if (initial) playerRegions else null,
            clientTile = if (initial) player.tile.id else null,
        )
    }

    fun updateDynamic(player: Player, initial: Boolean, force: Boolean) {
        val viewport = player.viewport ?: return

        val xteaList = mutableListOf<IntArray>()
        val zones = mutableListOf<Int?>()

        val view = player.tile.zone.minus(viewport.zoneRadius, viewport.zoneRadius)
        val zoneSize = viewport.zoneArea
        var append = 0
        for (origin in view.toCuboid(zoneSize, zoneSize).copy(minLevel = 0, maxLevel = 3).toZones()) {
            val target = dynamicZones.getDynamicZone(origin)
            if (target == null) {
                zones.add(null)
                continue
            }
            zones.add(target)
            val xtea = blankXtea
            if (!xteaList.contains(xtea)) {
                xteaList.add(xtea)
            } else {
                append++
            }
        }
        for (i in 0 until append) {
            xteaList.add(blankXtea)
        }
        viewport.dynamic = true
        player.client?.dynamicMapRegion(
            zoneX = player.tile.zone.x,
            zoneY = player.tile.zone.y,
            forceRefresh = force,
            mapSize = Viewport.VIEWPORT_SIZES.indexOf(viewport.tileSize),
            zones = zones,
            xteas = xteaList.toTypedArray(),
            clientIndex = if (initial) player.index - 1 else null,
            playerRegions = if (initial) playerRegions else null,
            clientTile = if (initial) player.tile.id else null,
        )
    }
}

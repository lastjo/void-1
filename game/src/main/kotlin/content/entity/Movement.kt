package content.entity

import content.area.misthalin.Border
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.PauseMode
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.map.collision.Collisions
import world.gregs.voidps.network.client.instruction.Walk
import world.gregs.voidps.type.Distance.nearestTo
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import world.gregs.voidps.type.area.Rectangle
import world.gregs.voidps.type.sub.*

class Movement(
    private val collisions: Collisions,
    private val npcs: NPCs,
    private val players: Players,
    private val areas: AreaDefinitions,
) {
    val borders = mutableMapOf<Zone, Rectangle>()

    @Instruction(Walk::class)
    fun walk(player: Player, instruction: Walk) {
        if (player.contains("delay")) {
            return
        }
        player.closeInterfaces()
        player.clearWatch()
        player.queue.clearWeak()
        player.suspension = null
        if (instruction.minimap && !player["a_world_in_microcosm_task", false]) {
            player["a_world_in_microcosm_task"] = true
        }

        val target = player.tile.copy(instruction.x, instruction.y)
        val border = borders[target.zone]
        if (border != null && (target in border || player.tile in border)) {
            val tile = border.nearestTo(player.tile)
            val endSide = Border.getOppositeSide(border, tile)
            player.walkTo(endSide, noCollision = true, forceWalk = true)
        } else {
            if (player.tile == target && player.mode != EmptyMode && player.mode != PauseMode) {
                player.mode = EmptyMode
            }
            player.walkTo(target)
        }
    }

    @Spawn
    fun spawn(world: World) {
        for (border in areas.getTagged("border")) {
            val passage = border.area as Rectangle
            for (zone in passage.toZones()) {
                borders[zone] = passage
            }
        }
    }

    @Spawn
    fun spawn(player: Player) {
        if (players.add(player) && Settings["world.players.collision", false]) {
            add(player)
        }
    }

    @Despawn
    fun despawn(player: Player) {
        if (Settings["world.players.collision", false]) {
            remove(player)
        }
    }

    @Spawn
    fun spawn(npc: NPC) {
        if (Settings["world.npcs.collision", false]) {
            add(npc)
        }
    }

    @Move
    fun move(npc: NPC, from: Tile, to: Tile) {
        npcs.update(from, to, npc)
    }

    @Death
    fun death(npc: NPC) {
        remove(npc)
    }

    @Despawn
    fun despawn(npc: NPC) {
        if (Settings["world.npcs.collision", false]) {
            remove(npc)
        }
    }

    fun add(char: Character) {
        val mask = char.collisionFlag
        val size = char.size
        for (x in char.tile.x until char.tile.x + size) {
            for (y in char.tile.y until char.tile.y + size) {
                collisions.add(x, y, char.tile.level, mask)
            }
        }
    }

    fun remove(char: Character) {
        val mask = char.collisionFlag
        val size = char.size
        for (x in 0 until size) {
            for (y in 0 until size) {
                collisions.remove(char.tile.x + x, char.tile.y + y, char.tile.level, mask)
            }
        }
    }
}

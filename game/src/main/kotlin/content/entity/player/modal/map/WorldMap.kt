package content.entity.player.modal.map

import content.entity.effect.frozen
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.hasOpen
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.network.client.instruction.WorldMapClick
import world.gregs.voidps.network.login.protocol.encode.updateInterface
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.*

class WorldMap(private val definitions: InterfaceDefinitions) {

    @Instruction(WorldMapClick::class)
    fun click(player: Player, instruction: WorldMapClick) {
        val tile = instruction.tile
        if (player.hasClock("world_map_double_click") && player["previous_world_map_click", 0] == tile) {
            player["world_map_marker_custom"] = tile
        }
        player["previous_world_map_click"] = tile
        player.start("world_map_double_click", 1)
    }

    @Open("world_map")
    fun open(player: Player) {
        updateMap(player)
        if (player.steps.isNotEmpty()) {
            player.softTimers.start("world_map_check")
        }
        player.sendVariable("world_map_hide_player_location")
        player.sendVariable("world_map_hide_links")
        player.sendVariable("world_map_hide_labels")
        player.sendVariable("world_map_hide_tooltips")
        player.sendVariable("world_map_marker_custom")
        player.interfaceOptions.unlockAll("world_map", "key_list", 0..182)
    }

    @TimerStart("world_map_check")
    fun start(player: Player): Int {
        return 5
    }

    @TimerTick("world_map_check")
    fun tick(player: Player): Int {
        updateMap(player)
        if (player.steps.isEmpty() || !player.hasOpen("world_map")) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }

    @Interface("Re-sort key", "order", "world_map")
    fun sort(player: Player) {
        player["world_map_list_order"] = when (player["world_map_list_order", "categorised"]) {
            "categorised" -> "traditional"
            "traditional" -> "alphabetical"
            "alphabetical" -> "categorised"
            else -> "categorised"
        }
    }

    @Interface(id = "world_map", component = "key_list")
    fun click(player: Player, itemSlot: Int) {
        when (itemSlot) {
            1 -> player.toggle("world_map_hide_player_location")
            4 -> player.toggle("world_map_hide_links")
            12 -> player.toggle("world_map_hide_labels")
            16 -> player.toggle("world_map_hide_tooltips")
            19 -> player["world_map_marker_custom"] = 0
        }
    }

    @Interface("Clear marker", "marker", "world_map")
    fun clear(player: Player) {
        player["world_map_marker_custom"] = 0
    }

    @Interface(component = "world_map", id = "toplevel*")
    fun worldMap(player: Player) {
        if (player.frozen) {
            player.message("You cannot do this at the moment.") // TODO proper message
        } else {
            player.open("world_map")
        }
    }

    @Interface(component = "close", id = "world_map")
    fun close(player: Player) {
        // Mechanics are unknown, would need tracking last interface to handle inside Interfaces.kt
        player.client?.updateInterface(definitions.get(player.interfaces.gameFrame).id, 2)
        player.open(player.interfaces.gameFrame, close = false)
    }

    fun updateMap(player: Player) {
        val tile = player.tile.id
        player["world_map_centre"] = tile
        player["world_map_marker_player"] = tile
    }
}

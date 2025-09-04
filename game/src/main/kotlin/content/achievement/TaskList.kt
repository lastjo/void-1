package content.achievement

import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.data.definition.VariableDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.Variable

class TaskList(
    private val variables: VariableDefinitions,
    private val enumDefinitions: EnumDefinitions,
) {

    @Spawn
    fun setup(player: Player) {
        player.sendVariable("task_disable_popups")
        player["task_popup"] = 0
        player["task_previous_popup"] = 0
        var total = 0
        for (area in 0 until 8) {
            Tasks.forEach(area) {
                if (Tasks.isCompleted(player, definition.stringId)) {
                    player.sendVariable(definition.stringId)
                    total++
                }
                null
            }
        }
        player["task_progress_overall"] = total
        player.sendVariable("task_hide_completed")
        player.sendVariable("task_filter_sets")
    }

    @Open("task_list")
    fun openTasks(player: Player) {
        player.interfaceOptions.unlockAll("task_list", "tasks", 0..492)
        refresh(player)
    }

    @Interface("Select", "area_*", "task_list")
    fun select(player: Player, component: String) {
        player["task_list_area"] = component.removePrefix("area_")
        refresh(player)
    }

    @Interface("Summary", "tasks", "task_list")
    fun summary(player: Player, itemSlot: Int) {
        player["task_slot_selected"] = itemSlot / 4
    }

    @Interface("Pin", "tasks", "task_list")
    fun pinSlot(player: Player, itemSlot: Int) {
        pin(player, itemSlot / 4)
    }

    @Interface("Filter-sets", "filter_sets", "task_list")
    fun filterSets(player: Player) {
        player["task_filter_sets"] = !player["task_filter_sets", false]
    }

    @Interface("Filter-done", "filter_done", "task_list")
    fun filterDone(player: Player) {
        player["task_hide_completed"] = !player["task_hide_completed", false]
    }

    @Interface("Turn-off", "toggle_popups", "task_list")
    fun togglePopups(player: Player) {
        val disable = !player["task_disable_popups", false]
        player["task_disable_popups"] = disable
        if (disable) {
            player["task_popup"] = 0
            player["task_previous_popup"] = 0
        }
    }

    @Variable("task_pin_slot")
    fun pinChanged(player: Player) {
        player.close("task_list")
    }

    @Interface("Pin", "pin", "task_list")
    fun pinSelected(player: Player) {
        pin(player, player["task_slot_selected", 0])
    }

    fun indexOfSlot(player: Player, slot: Int): Int? {
        var count = 0
        return Tasks.forEach(areaId(player)) {
            if (player["task_hide_completed", false] && Tasks.isCompleted(player, definition.stringId)) {
                return@forEach null
            }
            if (player["task_filter_sets", false] && !definition.contains("task_sprite_offset")) {
                return@forEach null
            }
            if (count++ == slot) {
                return@forEach index
            }
            null
        }
    }

    fun find(player: Player, id: Int): Int {
        for (i in 0 until 6) {
            if (player["task_slot_$i", -1] == id) {
                return i
            }
        }
        return 1
    }

    fun refresh(player: Player) {
        player.sendVariable("task_list_area")
        val id = areaId(player)
        player.sendScript("task_main_list_populate", id, 999, 999)
        refreshCompletedCount(player)
    }

    fun areaId(player: Player) = variables.get("task_list_area")!!.values.toInt(player["task_list_area", "unstable_foundations"])

    fun pin(player: Player, slot: Int) {
        val index = indexOfSlot(player, slot) ?: return
        if (player["task_pinned", -1] == index) {
            player.clear("task_pinned")
            player.clear("task_pin_slot")
        } else {
            player["task_pinned"] = index
            player["task_pin_slot"] = find(player, index)
        }
    }

    fun refreshCompletedCount(player: Player) {
        var total = 0
        var completed = 0
        Tasks.forEach(areaId(player)) {
            if (Tasks.isCompleted(player, definition.stringId)) {
                completed++
                player.sendVariable(definition.stringId)
            }
            total++
            null
        }
        player["task_progress_current"] = completed
        player["task_progress_total"] = total
    }

    /*
        Hints
     */

    @Interface("Hint", "hint_*", "task_list")
    fun hint(player: Player, component: String) {
        val selected = player["task_slot_selected", 0]
        val index = indexOfSlot(player, selected) ?: return
        val tile: Int = enumDefinitions.getStructOrNull("task_structs", index, component.replace("hint_", "task_hint_tile_")) ?: return
        // TODO I expect the functionality is actually minimap highlights not world map
        player["world_map_marker_1"] = tile
        player["world_map_marker_text_1"] = ""
        player.open("world_map")
    }
}

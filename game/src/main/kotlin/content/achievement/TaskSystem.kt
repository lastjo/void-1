package content.achievement

import content.entity.player.modal.Tab
import content.entity.player.modal.tab
import content.quest.questCompleted
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.data.definition.StructDefinitions
import world.gregs.voidps.engine.data.definition.VariableDefinitions
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.*

class TaskSystem(
    private val variables: VariableDefinitions,
    private val enumDefinitions: EnumDefinitions,
    private val structDefinitions: StructDefinitions,
) {

    @Open("task_system")
    fun openTasks(player: Player) {
        player.sendVariable("task_pin_slot")
        player.sendVariable("task_pinned")
        player.sendVariable("introducing_explorer_jack_task")
        refreshSlots(player)
        if (player.contains("task_dont_show_again")) {
            player.sendVariable("task_dont_show_again")
        }
        if (!player.questCompleted("unstable_foundations")) {
            player["task_pinned"] = 3520 // Talk to explorer jack
            player["task_pin_slot"] = 1
            player["task_slot_selected"] = 1
            player["unstable_foundations"] = "incomplete"
        }
    }

    @Enter("lumbridge")
    fun enterLumbridge(player: Player) {
        player["task_area"] = "lumbridge_draynor"
    }

    @Enter("draynor")
    fun enterDraynor(player: Player) {
        player["task_area"] = "lumbridge_draynor"
    }

    @Exit("draynor")
    @Exit("lumbridge")
    fun exitAreas(player: Player) {
        player["task_area"] = "dnd_activities"
    }

    @Interface("Close", "close_hint", "task_system")
    fun closeHints(player: Player, id: String) {
        player.interfaces.sendVisibility(id, "message_overlay", false)
    }

    @Interface("Select Task", "task_*", "task_system")
    fun selectTask(player: Player, component: String) {
        val slot = component.removePrefix("task_").toInt()
        player["task_slot_selected"] = slot
    }

    @Interface("Select Task", "dont_show", "task_system")
    fun toggleTask(player: Player) {
        player["task_dont_show_again"] = !player["task_dont_show_again", false]
    }

    @Interface("Open", "task_list", "task_system")
    fun openList(player: Player) {
        player.open("task_list")
    }

    @Interface("OK", "ok", "task_system")
    fun closeSummary(player: Player) {
        player.interfaces.sendVisibility("task_system", "summary_overlay", false)
        val slot = player["task_slot_selected", 0]
        val selected = indexOfSlot(player, slot) ?: return
        if (selected == player["task_pinned", -1]) {
            player.clear("task_pinned")
            player.clear("task_pin_slot")
        }
        player.interfaces.sendVisibility("task_system", "ok", false)
        refreshSlots(player)
    }

    @Interface("Pin/Unpin Task", "task_*", "task_system")
    fun pinSummary(player: Player, component: String) {
        val index = component.removePrefix("task_").toInt()
        pin(player, index)
    }

    @Interface("Set", "pin", "task_system")
    fun pin(player: Player, component: String) {
        val slot = player.get<Int>("task_slot_selected") ?: return
        pin(player, slot)
        player.interfaces.sendVisibility("task_system", "summary_overlay", false)
    }

    @Variable("task_pin_slot", "task_area")
    fun pinChanged(player: Player) {
        refreshSlots(player)
    }

    @Interface("Details", "details", "task_popup")
    fun details(player: Player) {
        if (player.questCompleted("unstable_foundations")) {
            player["task_popup_summary"] = true
            player.interfaces.sendVisibility("task_system", "ok", true)
            val index = player["task_popup", -1]
            for (slot in 0 until 6) {
                if (player["task_slot_$slot", -1] == index) {
                    player["task_slot_selected"] = slot
                    break
                }
            }
        }
        player.tab(Tab.TaskSystem)
    }

    @Variable("*_task")
    fun taskSet(player: Player, key: String, to: Any?) {
        if (to == true || to == "completed") {
            completeTask(player, key)
        }
    }

    @Interface("Hint", "hint_*", "task_system")
    fun hint(player: Player, component: String) {
        val selected = player["task_slot_selected", 0]
        val index = indexOfSlot(player, selected) ?: return
        val tile: Int = enumDefinitions.getStructOrNull("task_structs", index, component.replace("hint_", "task_hint_tile_")) ?: return
        // TODO I expect the functionality is actually minimap highlights not world map
        player["world_map_marker_1"] = tile
        player["world_map_marker_text_1"] = ""
        player.open("world_map")
    }

    fun pin(player: Player, slot: Int) {
        if (player["task_pin_slot", -1] == slot) {
            player.clear("task_pinned")
            player.clear("task_pin_slot")
        } else {
            player["task_pinned"] = indexOfSlot(player, slot) ?: return
            player["task_pin_slot"] = slot
        }
    }

    fun indexOfSlot(player: Player, slot: Int): Int? {
        var count = 1
        return Tasks.forEach(areaId(player)) {
            val hideCompleted = Tasks.isCompleted(player, definition.stringId)
            val hideMembers = definition["task_members", 0] == 1 && !World.members
            if (hideCompleted || hideMembers) {
                return@forEach null
            }
            if (count == player["task_pin_slot", -1]) {
                val pinned = player["task_pinned", 4091]
                if (count == slot) {
                    return@forEach pinned
                }
                skip = pinned != index
            }
            if (count++ == slot) {
                return@forEach index
            }
            null
        }
    }

    fun refreshSlots(player: Player) {
        var slot = 1
        var completed = 0
        var total = 0
        Tasks.forEach(areaId(player)) {
            total++
            val pinned = pinned(player, slot)
            if (player["task_pinned", -1] == index && !pinned || !Tasks.hasRequirements(player, definition)) {
                return@forEach null
            }
            if (Tasks.isCompleted(player, definition.stringId)) {
                completed++
                return@forEach null
            }
            if (pinned) {
                player["task_slot_${slot++}"] = player["task_pinned", 4091]
                total--
                skip = true
            } else if (slot < 7) {
                player["task_slot_${slot++}"] = index
            }
            null
        }
        if (slot < 7) {
            for (i in slot..6) {
                player["task_slot_$i"] = 4091
            }
        }
        player["task_progress_total"] = total
        player["task_progress_current"] = completed
    }

    fun pinned(player: Player, slot: Int): Boolean {
        val pinned = player["task_pin_slot", -1]
        return pinned != -1 && slot == pinned
    }

    fun areaId(player: Player) = variables.get("task_area")!!.values.toInt(player["task_area", "empty"])

    /*
        Task completion
     */

    fun completeTask(player: Player, id: String) {
        val definition = structDefinitions.get(id)
        val index = definition["task_index", -1]
        player["task_popup"] = index
        val difficulty = definition["task_difficulty", 0]
        val area = definition["task_area", 61]
        val areaName = enumDefinitions.get("task_area_names").getString(area)
        val difficultyName = enumDefinitions.get("task_difficulties").getString(difficulty)
        if (areaName.isNotBlank() && difficultyName.isNotBlank()) {
            player.message("You have completed the Task '${definition["task_name", ""]}' in the $difficultyName $areaName set!")
        } else {
            player.message("You have completed the Task '${definition["task_name", ""]}'!")
        }
        val before = player["task_progress_current", 0]
        refreshSlots(player)
        val total = player.inc("task_progress_overall")
        player.message("You have now completed $total ${"Task".plural(total)} in total.")
        val after = player["task_progress_current", 0]
        val maximum = player["task_progress_total", -1]
        if (before != after && after == maximum) {
            val prettyName = when (area) {
                1 -> "Lumbridge and Draynor"
                else -> areaName
            }
            player.message("Congratulations! You have completed all of the $difficultyName Tasks in the $prettyName")
            val npc = when {
                area == 1 && difficulty == 1 -> "Explorer Jack in Lumbridge"
                area == 1 && difficulty == 2 -> "Bob in Bob's Axes in Lumbridge"
                area == 1 && difficulty == 3 -> "Ned in Draynor Village"
                else -> "someone somewhere"
            }
            player.message("set. Speak to $npc to claim your reward.")
        }
    }

    /*
        Hints
     */
}

package content.skill.magic.book

import world.gregs.voidps.engine.client.ui.chat.toInt
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Spawn

class SpellBookFilter {

    @Open("*_spellbook")
    fun open(player: Player, id: String) {
        val index = when (id) {
            "ancient_spellbook" -> 1
            "lunar_spellbook" -> 2
            "dungeoneering_spellbook" -> 3
            else -> 0
        }
        player["spellbook_config"] = index or (player["defensive_cast", false].toInt() shl 8)
    }

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("spellbook_sort")
        player.sendVariable("spellbook_config")
    }

    @Interface(component = "filter_*", id = "*_spellbook")
    fun filter(player: Player, id: String, component: String) {
        val key = "spellbook_sort"
        val combined = "$id:$component"
        if (player.containsVarbit(key, combined)) {
            player.removeVarbit(key, combined)
        } else {
            player.addVarbit(key, combined)
        }
    }

    @Interface(component = "sort_*", id = "*_spellbook")
    fun sort(player: Player, id: String, component: String) {
        val key = "spellbook_sort"
        if (component.startsWith("sort_")) {
            // Make sure don't sort by multiple at once
            player.removeVarbit(key, "${id}_sort_combat", refresh = false)
            player.removeVarbit(key, "${id}_sort_teleport", refresh = false)
        }
        if (component != "sort_level") {
            player.addVarbit(key, "$id:$component", refresh = false)
        }
    }

    @Interface("Defensive Casting", "defensive_cast", "*_spellbook")
    fun toggle(player: Player, component: String) {
        player.toggle(component)
    }

}

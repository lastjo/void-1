package content.quest.member.fairy_tale_part_2.fairy_ring

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class TravelLog(private val fairyRing: FairyRingCodes) {

    @Interface("Re-sort list", "re_sort_list", "travel_log")
    fun sort(player: Player) {
        player.toggle("travel_log_re_sort")
    }

    @Interface(id = "travel_log")
    fun option(player: Player, component: String) {
        player["fairy_ring_code_1"] = component[0].toString()
        player["fairy_ring_code_2"] = component[1].toString()
        player["fairy_ring_code_3"] = component[2].toString()
    }

    @Open("travel_log")
    fun open(player: Player, id: String) {
        player.sendVariable("travel_log_re_sort")
        val list: List<String> = player["travel_log_locations"] ?: return
        for ((code, def) in fairyRing.codes) {
            if (list.contains(code)) {
                player.interfaces.sendText(id, def.id.lowercase(), "<br>${def.name}")
            }
        }
    }
}

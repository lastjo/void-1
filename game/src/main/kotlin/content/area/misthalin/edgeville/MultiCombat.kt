package content.area.misthalin.edgeville

import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Enter
import world.gregs.voidps.type.sub.Exit
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.Variable

class MultiCombat(private val areaDefinitions: AreaDefinitions) {

    @Spawn
    fun spawn(npc: NPC) {
        for (def in areaDefinitions.get(npc.tile.zone)) {
            if (def.tags.contains("multi_combat")) {
                npc["in_multi_combat"] = true
                break
            }
        }
    }

    @Enter(tag = "multi_combat")
    fun enter(player: Player) {
        player["in_multi_combat"] = true
    }

    @Exit(tag = "multi_combat")
    fun exit(player: Player) {
        player.clear("in_multi_combat")
    }

    @Variable("in_multi_combat", toBool = "true")
    @Variable("in_multi_combat", toNull = true)
    fun set(player: Player, to: Any?) {
        player.interfaces.sendVisibility("area_status_icon", "multi_combat", to as? Boolean ?: false)
    }
}

package content.area.troll_country.god_wars_dungeon

import content.entity.combat.killer
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.PlayerOption
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.*

class GodwarsAggression(areas: AreaDefinitions) {

    val dungeon = areas["godwars_dungeon_multi_area"]

    @Enter("godwars_dungeon_multi_area")
    fun enter(player: Player) {
        player.open("godwars_overlay")
        player["gods"] = player.equipment.items.mapNotNull { it.def.getOrNull<String>("god") }.toMutableSet()
    }

    @Open("godwars_overlay")
    fun open(player: Player) {
        player.sendVariable("armadyl_killcount")
        player.sendVariable("bandos_killcount")
        player.sendVariable("saradomin_killcount")
        player.sendVariable("zamorak_killcount")
        player.sendVariable("godwars_darkness")
    }

    @Exit("godwars_dungeon_multi_area")
    fun exit(player: Player, logout: Boolean) {
        player.close("godwars_overlay")
        if (logout) {
            return
        }
        player["godwars_darkness"] = false
        player.clear("armadyl_killcount")
        player.clear("bandos_killcount")
        player.clear("saradomin_killcount")
        player.clear("zamorak_killcount")
    }

    @ItemAdded(inventory = "worn_equipment")
    fun equipped(player: Player, item: Item) {
        val god = item.def.getOrNull<String>("god") ?: return
        if (player.tile in dungeon) {
            player.get<MutableSet<String>>("gods")!!.add(god)
        }
    }

    @ItemRemoved(inventory = "worn_equipment")
    fun removed(player: Player) {
        if (player.tile in dungeon) {
            player["gods"] = player.equipment.items.mapNotNull { it.def.getOrNull<String>("god") }.toMutableSet()
        }
    }

    @Spawn
    fun spawn(npc: NPC) {
        randomHuntMode(npc)
    }

    @Death
    fun despawn(npc: NPC) {
        val killer = npc.killer
        if (killer is NPC) {
            randomHuntMode(npc)
        } else if (killer is Player) {
            val god = npc.def["god", ""]
            if (god != "") {
                killer.inc("${god}_killcount")
            }
        }
    }

    @Hunt("godwars_aggressive")
    fun huntPlayers(npc: NPC, target: Player) {
        npc.mode = Interact(npc, target, PlayerOption(npc, target, "Attack"))
    }

    @Hunt("zamorak_aggressive")
    fun huntOtherGods(npc: NPC, target: NPC) {
        npc.mode = Interact(npc, target, NPCOption(npc, target, target.def, "Attack"))
    }

    @Hunt("anti_zamorak_aggressive")
    fun huntZamorak(npc: NPC, target: NPC) {
        npc.mode = Interact(npc, target, NPCOption(npc, target, target.def, "Attack"))
    }

    fun randomHuntMode(npc: NPC) {
        if (npc.tile in dungeon && (npc.def["hunt_mode", ""] == "zamorak_aggressive" || npc.def["hunt_mode", ""] == "anti_zamorak_aggressive")) {
            npc["hunt_mode"] = if (random.nextBoolean()) npc.def["hunt_mode"] else "godwars_aggressive"
        }
    }
}

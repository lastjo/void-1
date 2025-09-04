package content.entity.npc.combat

import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.combat.CombatMovement
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.PlayerOption
import world.gregs.voidps.type.sub.Hunt

class Aggression(
    private val areas: AreaDefinitions,
) {

    @Hunt("aggressive")
    @Hunt("aggressive_intolerant")
    fun hunt(npc: NPC, target: Player) {
        if (!Settings["world.npcs.aggression", true] || attacking(npc, target)) {
            return
        }
        if (Settings["world.npcs.safeZone", false] && npc.tile in areas["lumbridge"]) {
            return
        }
        npc.mode = Interact(npc, target, PlayerOption(npc, target, "Attack"))
    }

    @Hunt("cowardly")
    fun coward(npc: NPC, target: Player) {
        if (!Settings["world.npcs.aggression", true] || attacking(npc, target)) {
            return
        }
        if (Settings["world.npcs.safeZone", false] && npc.tile in areas["lumbridge"]) {
            return
        }
        npc.mode = Interact(npc, target, PlayerOption(npc, target, "Attack"))
    }

    @Hunt("aggressive")
    @Hunt("aggressive_intolerant")
    fun hunt(npc: NPC, target: NPC) {
        if (!attacking(npc, target)) {
            npc.mode = Interact(npc, target, NPCOption(npc, target, target.def, "Attack"))
        }
    }

    @Hunt("cowardly")
    fun coward(npc: NPC, target: NPC) {
        if (attacking(npc, target)) {
            return
        }
        npc.mode = Interact(npc, target, NPCOption(npc, target, target.def, "Attack"))
    }

    fun attacking(npc: NPC, target: Character): Boolean {
        val current = npc.mode
        if (current is Interact && current.target == target) {
            return true
        } else if (current is CombatMovement && current.target == target) {
            return true
        }
        return false
    }
}

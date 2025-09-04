package content.entity.npc.combat

import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.combat.CombatMovement
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
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
        val block: suspend (Boolean) -> Unit = { Publishers.all.npcPlayerOption(npc, target, "Attack", it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasNPCPlayerOption(npc, target, "Attack", it) }
        npc.mode = Interact(npc, target, interact = block, has = check)
    }

    @Hunt("cowardly")
    fun coward(npc: NPC, target: Player) {
        if (!Settings["world.npcs.aggression", true] || attacking(npc, target)) {
            return
        }
        if (Settings["world.npcs.safeZone", false] && npc.tile in areas["lumbridge"]) {
            return
        }
        val block: suspend (Boolean) -> Unit = { Publishers.all.npcPlayerOption(npc, target, "Attack", it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasNPCPlayerOption(npc, target, "Attack", it) }
        npc.mode = Interact(npc, target, interact = block, has = check)
    }

    @Hunt("aggressive")
    @Hunt("aggressive_intolerant")
    fun hunt(npc: NPC, target: NPC) {
        if (!attacking(npc, target)) {
            val block: suspend (Boolean) -> Unit = { Publishers.all.npcNPCOption(npc, target, target.def, "Attack", it) }
            val check: (Boolean) -> Boolean = { Publishers.all.hasNPCNPCOption(npc, target, target.def, "Attack", it) }
            npc.mode = Interact(npc, target, interact = block, has = check)
        }
    }

    @Hunt("cowardly")
    fun coward(npc: NPC, target: NPC) {
        if (attacking(npc, target)) {
            return
        }
        val block: suspend (Boolean) -> Unit = { Publishers.all.npcNPCOption(npc, target, target.def, "Attack", it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasNPCNPCOption(npc, target, target.def, "Attack", it) }
        npc.mode = Interact(npc, target, interact = block, has = check)
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

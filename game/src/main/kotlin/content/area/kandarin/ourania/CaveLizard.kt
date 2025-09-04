package content.area.kandarin.ourania

import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCOption
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.PlayerOption
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Hunt
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick

class CaveLizard {

    @Spawn("cave_lizard")
    fun spawn(npc: NPC) {
        npc.softTimers.start("aggressive_hunt_mode_switch")
    }

    @Hunt("aggressive_npcs", "cave_lizard", "zamorak_*")
    fun huntNPC(npc: NPC, target: NPC) {
        npc.mode = Interact(npc, target, NPCOption(npc, target, target.def, "Attack"))
    }

    @Hunt("aggressive", "cave_lizard")
    fun huntPlayer(npc: NPC, target: Player) {
        npc.mode = Interact(npc, target, PlayerOption(npc, target, "Attack"))
    }

    @TimerStart("aggressive_hunt_mode_switch")
    fun start(player: Player): Int = random.nextInt(6, 12)

    @TimerTick("aggressive_hunt_mode_switch")
    fun tick(npc: NPC): Int {
        npc["hunt_mode"] = if (random.nextBoolean()) "aggressive" else "aggressive_npcs"
        return TimerState.CONTINUE
    }
}

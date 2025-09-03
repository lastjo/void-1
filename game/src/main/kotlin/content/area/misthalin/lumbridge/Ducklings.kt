package content.area.misthalin.lumbridge

import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.Follow
import world.gregs.voidps.engine.entity.character.mode.Wander
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.queue.softQueue
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Death
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick

class Ducklings(private val npcs: NPCs) {

    @Death("duck*swim")
    fun death(npc: NPC) {
        val ducklings: NPC = npc["ducklings"] ?: return
        ducklings.say("Eek!")
        followParent(ducklings)
    }

    @TimerStart("follow_parent")
    fun start(npc: NPC): Int = 0

    @TimerTick("follow_parent")
    fun tick(npc: NPC): Int {
        if (npc.mode != EmptyMode && npc.mode !is Wander) {
            return TimerState.CONTINUE
        }
        val parent = findParent(npc) ?: return TimerState.CONTINUE
        npc.mode = Follow(npc, parent)
        parent["ducklings"] = npc
        if (random.nextInt(300) < 1) {
            parent.say("Quack?")
            npc.softQueue("quack", 1) {
                npc.say(if (random.nextBoolean()) "Cheep Cheep!" else "Eep!")
            }
        }
        return TimerState.CANCEL
    }

    fun isDuck(it: NPC) = it.id.startsWith("duck") && it.id.endsWith("swim")

    @Spawn("ducklings")
    fun followParent(npc: NPC) {
        npc.softTimers.start("follow_parent")
    }

    fun findParent(npc: NPC): NPC? {
        for (dir in Direction.cardinal) {
            return npcs[npc.tile.add(dir.delta)].firstOrNull { isDuck(it) && !it.contains("ducklings") } ?: continue
        }
        return null
    }
}

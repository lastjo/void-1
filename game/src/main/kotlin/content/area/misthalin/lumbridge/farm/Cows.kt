package content.area.misthalin.lumbridge.farm

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnNPCOperate
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.npcSpawn
import world.gregs.voidps.engine.timer.npcTimerStart
import world.gregs.voidps.engine.timer.npcTimerTick
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import world.gregs.voidps.type.sub.UseOn

class Cows {

    @Spawn("cow*")
    fun spawn(npc: NPC) {
        npc.softTimers.start("eat_grass")
    }

    @TimerStart("eat_grass")
    fun start(npc: NPC): Int {
        npc.mode = EmptyMode
        return random.nextInt(50, 200)
    }

    @TimerTick("eat_grass")
    fun tick(npc: NPC) {
        if (npc.mode == EmptyMode) {
            npc.say("Moo")
            npc.anim("cow_eat_grass")
        }
    }

    @UseOn(on = "cow*")
    fun use(player: Player, target: NPC) {
        player.message("The cow doesn't want that.")
    }

}

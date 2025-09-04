package content.entity.player.effect.energy

import world.gregs.voidps.cache.definition.data.NPCDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.mode.Rest
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Option

class Resting {

    @Interface("Rest", id = "energy_orb")
    fun rest(player: Player) {
        if (player["movement", "walk"] == "rest") {
            player.message("You are already resting.")
        } else {
            player.mode = Rest(player, -1)
        }
    }

    @Option("Listen-to")
    suspend fun operate(player: Player, target: NPC, def: NPCDefinition) {
        player.arriveDelay()
        if (def["song", -1] != -1) {
            player.mode = Rest(player, def["song"])
        }
    }
}

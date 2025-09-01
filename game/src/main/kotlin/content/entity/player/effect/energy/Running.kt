package content.entity.player.effect.energy

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendRunEnergy
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.Rest
import world.gregs.voidps.engine.entity.character.move.running
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Spawn

class Running {

    @Open("energy_orb")
    fun open(player: Player) {
        player.sendRunEnergy(player.energyPercent())
    }

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("movement")
    }

    @Interface("Turn Run mode on", id = "energy_orb")
    fun run(player: Player) {
        if (player.mode is Rest) {
            val walking = player["movement", "walk"] == "walk"
            toggleRun(player, !walking)
            player["movement_temp"] = if (walking) "run" else "walk"
            player.mode = EmptyMode
            return
        }
        toggleRun(player, player.running)
    }

    fun toggleRun(player: Player, run: Boolean) {
        val energy = player.energyPercent()
        if (energy == 0) {
            player.message("You don't have enough energy left to run!", ChatType.Filter)
        }
        player.running = !run && energy > 0
    }
}

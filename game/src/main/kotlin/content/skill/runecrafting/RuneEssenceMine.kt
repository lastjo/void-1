package content.skill.runecrafting

import content.entity.proj.shoot
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.queue.softQueue
import world.gregs.voidps.type.sub.Option

class RuneEssenceMine(private val areas: AreaDefinitions) {

    @Option("Enter", "rune_essence_exit_portal")
    fun enter(player: Player, target: GameObject) {
        player.message("You step through the portal...")
        player.gfx("curse_impact", delay = 30)
        target.tile.shoot("curse", player.tile)

        player.softQueue("essence_mine_exit", 3) {
            val npc = player["last_npc_teleport_to_rune_essence_mine", "aubury"]
            val tile = areas["${npc}_return"].random()
            player.tele(tile)
        }
    }
}

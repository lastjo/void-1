package content.area.misthalin.barbarian_village.stronghold_of_security

import content.entity.obj.ObjectTeleports
import content.entity.player.dialogue.Shifty
import content.entity.player.dialogue.type.player
import content.entity.player.dialogue.type.statement
import content.entity.player.dialogue.type.warning
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.queue.queue
import world.gregs.voidps.type.equals
import world.gregs.voidps.type.sub.Teleport
import world.gregs.voidps.type.sub.TeleportLand

class StrongholdOfSecurityLadders(private val teleports: ObjectTeleports) {

    @TeleportLand("Climb-down", "stronghold_of_security_entrance")
    fun entrance(player: Player, target: GameObject) {
        player.queue("stronghold_of_security_entrance") {
            statement("You squeeze through the hole and find a ladder a few feet down leading into the Stronghold of Security.")
        }
    }

    @Teleport("Climb-up", "stronghold_war_ladder_up")
    fun ladder(player: Player, target: GameObject) {
        if (target.tile.equals(1859, 5244)) {
            player.message("You climb up the ladder to the surface above.")
        } else {
            player.message("You climb up the ladder which seems to twist and wind in all directions.")
        }
    }

    @Teleport("Climb-up", "stronghold_war_chain_up")
    fun chain(player: Player, target: GameObject) {
        player.message("You climb up the chain very very carefully, squeeze through a passage then climb a ladder.")
        player.message("You climb up the ladder which seems to twist and wind in all directions.")
    }

    @Teleport("Climb-down", "stronghold_war_ladder_down", "stronghold_famine_ladder_down")
    fun down(player: Player, target: GameObject, option: String): Int {
        if (player["warning_stronghold_of_security_ladders", 0] == 7) {
            return 0
        }
        player.queue("stronghold_warning") {
            if (!warning("stronghold_of_security_ladders")) {
                player<Shifty>("No thanks, I don't want to die!")
            } else {
                player.message("You climb down the ladder to the next level.")
                player.clear("stronghold_safe_space")
                val definition = teleports.get(option)[target.tile.id]!!
                teleports.teleportContinue(player, target, target.def, option, definition, 0)
            }
        }
        return -1
    }

    @Teleport("Climb-up", "stronghold_famine_rope_up", "stronghold_pestilence_vine_up", "stronghold_death_rope_up")
    fun ropeUp(player: Player, target: GameObject) {
        player.message("You shin up the rope, squeeze through a passage then climb a ladder.")
        player.message("You climb up the ladder which seems to twist and wind in all directions.")
    }

    @Teleport("Climb-up", "stronghold_famine_ladder_up", "stronghold_pestilence_ladder_up", "stronghold_death_ladder_up")
    fun ladderUp(player: Player, target: GameObject) {
        player.message("You climb up the ladder to the level above.")
    }
}

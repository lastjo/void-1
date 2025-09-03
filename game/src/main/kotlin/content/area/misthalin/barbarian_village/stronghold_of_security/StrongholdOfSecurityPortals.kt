package content.area.misthalin.barbarian_village.stronghold_of_security

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.combatLevel
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Teleport

class StrongholdOfSecurityPortals {

    @Teleport("Enter", "stronghold_war_portal")
    fun war(player: Player, target: GameObject): Int {
        return check(player, "unlocked_emote_flap", 25)
    }

    @Teleport("Enter", "stronghold_famine_portal")
    fun famine(player: Player, target: GameObject): Int {
        return check(player, "unlocked_emote_slap_head", 50)
    }

    @Teleport("Enter", "stronghold_pestilence_portal")
    fun pestilence(player: Player, target: GameObject): Int {
        return check(player, "unlocked_emote_idea", 75)
    }

    @Teleport("Enter", "stronghold_death_portal")
    fun death(player: Player, target: GameObject): Int {
        if (player["unlocked_emote_stomp", false]) {
            player.clear("stronghold_safe_space")
            player.message("You enter the portal to be whisked through to the treasure room.", ChatType.Filter)
            return 0
        }
        player.message("You must have completed this level to take this shortcut.")
        return -1
    }

    private fun check(player: Player, emote: String, combat: Int): Int {
        if (player[emote, false] || player.combatLevel > combat) {
            player.clear("stronghold_safe_space")
            player.message("You enter the portal to be whisked through to the treasure room.", ChatType.Filter)
            return 0
        }
        player.message("You are not of sufficient experience to take the shortcut through this level.")
        return -1
    }
}

package content.entity.player

import content.entity.combat.inCombat
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.AccountManager
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Interface

class Exit(private val accounts: AccountManager) {

    @Interface("Exit", "logout", "toplevel*")
    fun exit(player: Player) {
        player.open("logout")
    }

    @Interface(id = "logout")
    fun logout(player: Player) {
        if (player.inCombat) {
            player.message("You can't log out until 8 seconds after the end of combat.")
            return
        }
        accounts.logout(player, true)
    }
}

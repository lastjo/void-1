package content.skill.slayer

import net.pearx.kasechange.toSentenceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.Colours
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class SlayerAssignment {

    @Interface("Learn", "learn", "slayer_rewards_assignment")
    fun learn(player: Player) {
        player.open("slayer_rewards_learn")
    }

    @Interface("Buy", "buy", "slayer_rewards_assignment")
    fun buy(player: Player) {
        player.open("slayer_rewards")
    }

    @Interface("Reassign *", "reassign_*", "slayer_rewards_assignment")
     fun reassign(player: Player) {
        if (player.slayerPoints < 30) {
            player.message("Sorry. That would cost 30 and you only have ${player.slayerPoints} Slayer ${"Point".plural(player.slayerPoints)}.")
            return
        }
        if (player.slayerTask == "nothing") {
            player.message("You need a task in order to skip it.") // TODO proper message
            return
        }
        player.slayerPoints -= 30
        player.slayerTask = "nothing"
        //    npc<Happy>(player["slayer_npc", ""], "") TODO proper message and save npc id on interface open
    }

    @Interface("Permanently *", "block_*", "slayer_rewards_assignment")
    fun block(player: Player) {
        if (player.slayerPoints < 100) {
            player.message("Sorry. That would cost 100 and you only have ${player.slayerPoints} Slayer ${"Point".plural(player.slayerPoints)}.")
            return
        }
        if (player.slayerTask == "nothing") {
            player.message("You need a task in order to block it.") // TODO proper message
            return
        }
        var blocked = false
        for (i in 0 until 5) {
            if (!player.contains("blocked_task_$i")) {
                player["blocked_task_$i"] = player.slayerTask
                player.slayerTask = "nothing"
                player.slayerPoints -= 100
                blocked = true
                break
            }
        }

        if (!blocked) {
            player.message("You don't have any free block slots.") // TODO proper message
        }
    }

    @Open("slayer_rewards_assignment")
    fun refresh(player: Player, id: String) {
        val points = player.slayerPoints
        player.interfaces.sendText(id, "current_points", points.toString())
        player.interfaces.sendColour(id, "current_points", if (points == 0) Colours.RED else Colours.GOLD)
        var hasBlockedSlot = false
        for (i in 0 until 5) {
            if (!player.contains("blocked_task_$i")) {
                hasBlockedSlot = true
            }
            player.interfaces.sendText(id, "text_$i", player["blocked_task_$i", "nothing"].toSentenceCase())
        }
        val assignment = player["slayer_assignment", ""]
        if (assignment.isEmpty()) {
            player.interfaces.sendText(id, "reassign_text", "You must have an assignment to use this.")
            player.interfaces.sendText(id, "block_text", "You must have an assignment to use this.")
        } else {
            player.interfaces.sendText(id, "reassign_text", "Cancel task of $assignment.")
            player.interfaces.sendText(id, "block_text", "Never assign $assignment again.")
        }
        player.interfaces.sendColour(id, "reassign_text", if (points < 30) Colours.RED else Colours.ORANGE)
        player.interfaces.sendColour(id, "reassign_points", if (points < 30) Colours.RED else Colours.ORANGE)
        player.interfaces.sendColour(id, "block_text", if (points < 100 || !hasBlockedSlot) Colours.RED else Colours.ORANGE)
        player.interfaces.sendColour(id, "block_points", if (points < 100 || !hasBlockedSlot) Colours.RED else Colours.ORANGE)
    }
}

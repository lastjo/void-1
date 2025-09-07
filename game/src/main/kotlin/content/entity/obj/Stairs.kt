package content.entity.obj

import content.bot.bot
import content.bot.interact.navigation.resume
import content.bot.isBot
import content.entity.player.dialogue.type.choice
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Teleport

class Stairs(private val teleports: ObjectTeleports) {

    @Option("Climb", arrive = false)
    suspend fun climb(player: Player, target: GameObject, def: ObjectDefinition) {
        if (def.options?.filterNotNull()?.any { it.startsWith("Climb-") } != true) {
            return
        }
        player.dialogue {
            choice("What would you like to do?") {
                option("Go up the stairs.") {
                    teleports.teleport(player, target, def, "Climb-up")
                }
                option("Go down the stairs.") {
                    teleports.teleport(player, target, def, "Climb-down")
                }
                option("Never mind.")
            }
        }
    }

    @Teleport
    fun tele(player: Player, target: GameObject, obj: ObjectDefinition, option: String): Int {
        if (player.isBot) {
            player.bot.resume("move")
        }
        if (!obj.name.isLadder()) {
            return 0
        }
        val remaining = player.remaining("teleport_delay")
        if (remaining > 0) {
            return remaining
        } else if (remaining < 0) {
            player.anim(if (option == "Climb-down" || obj.stringId.endsWith("_down")) "climb_down" else "climb_up")
            player.start("teleport_delay", 2)
            return 2
        }
        return -1
    }

    fun String.isLadder() = contains("ladder", true) || contains("rope", true) || contains("chain", true) || contains("vine", true) || isTrapDoor()

    fun String.isTrapDoor(): Boolean {
        val name = replace(" ", "")
        return name.equals("trapdoor", true) || name.equals("manhole", true)
    }
}

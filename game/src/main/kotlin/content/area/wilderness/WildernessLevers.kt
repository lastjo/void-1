package content.area.wilderness

import content.entity.obj.ObjectTeleports
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.statement
import content.entity.sound.sound
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.queue.strongQueue
import world.gregs.voidps.engine.suspend.SuspendableContext
import world.gregs.voidps.type.sub.Teleport
import world.gregs.voidps.type.sub.TeleportLand

class WildernessLevers(private val teleports: ObjectTeleports) {

    @TeleportLand("Pull", "lever_*")
    fun tele(player: Player, target: GameObject, obj: ObjectDefinition) {
        player.sound("teleport_land")
        player.gfx("teleport_land_modern")
        player.anim("teleport_land_modern")
        val message: String = obj.getOrNull("land_message") ?: return
        player.message(message, ChatType.Filter)
    }

    @Teleport("Pull", "lever_*")
    fun tele(player: Player, target: GameObject, def: ObjectDefinition, option: String): Int {
        if (def.stringId == "lever_ardougne_edgeville" && player["wilderness_lever_warning", true]) {
            player.strongQueue("wilderness_lever_warning") {
                statement("Warning! Pulling the lever will teleport you deep into the Wilderness.")
                choice("Are you sure you wish to pull it?") {
                    option("Yes I'm brave.") {
                        pullLever(player, target, def, option)
                    }
                    option("Eeep! The Wilderness... No thank you.") {
                        return@option
                    }
                    option("Yes please, don't show this message again.") {
                        player["wilderness_lever_warning"] = false
                        pullLever(player, target, def, option)
                    }
                }
            }
            return -1
        }
        player.strongQueue("wilderness_lever_warning") {
            pullLever(player)
            val teleport = teleports.get(option)[target.tile.id]!!
            val tile = ObjectTeleports.calculate(teleport, player)
            player.delay(1)
            player.anim("teleport_modern")
            player.sound("teleport")
            player.gfx("teleport_modern")
            player.delay(3)
            player.tele(tile)
            Publishers.all.teleportLandGameObject(player, target, def, option)
        }
        return 0
    }

    fun pullLever(player: Player) {
        player.message("You pull the lever...", ChatType.Filter)
        player.anim("pull_lever")
        player.start("movement_delay", 3)
    }

    suspend fun SuspendableContext<Player>.pullLever(player: Player, target: GameObject, def: ObjectDefinition, option: String) {
        pullLever(player)
        delay(2)
        player.anim("teleport_modern")
        player.sound("teleport")
        player.gfx("teleport_modern")
        val definition = teleports.get("Pull")[target.tile.id]!!
        teleports.teleportContinue(player, target, def, option, definition, 3)
    }
}

package content.area.troll_country.god_wars_dungeon

import content.entity.obj.door.enterDoor
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class GodwarsDoors(private val areas: AreaDefinitions) {

    @Option("Open", "big_door_saradomin_closed", "big_door_bandos_closed", "big_door_armadyl_closed", "big_door_zamorak_closed")
    suspend fun operate(player: Player, target: GameObject) {
        val god = target.id.removePrefix("big_door_").removeSuffix("_closed")
        if (player.tile in areas["${god}_chamber"]) {
            player.enterDoor(target)
            return
        }

        if (player["${god}_killcount", 0] < 40) {
            player.message("You don't have enough kills to enter the lair of the gods.")
            return
        }
        player.dec("${god}_killcount", 40)
        player.enterDoor(target)
    }

}

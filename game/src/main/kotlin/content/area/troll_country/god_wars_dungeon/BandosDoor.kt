package content.area.troll_country.god_wars_dungeon

import content.entity.obj.door.doorTarget
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.remove
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.Option

class BandosDoor {

    @Option("Bang", "godwars_bandos_big_door")
    suspend fun operate(player: Player, target: GameObject) {
        if (player.tile.x >= target.tile.x) {
            if (!player.has(Skill.Strength, 70, message = true)) {
                return
            }
            if (!player.inventory.contains("hammer")) {
                player.message("You need a suitable hammer to ring the gong.")
                return
            }
            player.anim("godwars_hammer_bang")
            player.delay(3)
        }
        target.remove(ticks = 2, collision = false)
        player.walkOverDelay(doorTarget(player, target) ?: return)
    }
}

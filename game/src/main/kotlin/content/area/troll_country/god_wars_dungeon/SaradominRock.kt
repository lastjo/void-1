package content.area.troll_country.god_wars_dungeon

import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.sub.Enter
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class SaradominRock {

    @Enter("godwars_dungeon_multi_area")
    fun enter(player: Player) {
        player.sendVariable("godwars_saradomin_rope_top")
        player.sendVariable("godwars_saradomin_rope_bottom")
    }

    @Option("Tie-rope", "godwars_saradomin_rock_top", "godwars_saradomin_rock_bottom")
    fun tie(player: Player, target: GameObject, def: ObjectDefinition) {
        tieRope(player, def.stringId)
    }

    @UseOn("rope", "godwars_saradomin_rock_top", "godwars_saradomin_rock_bottom")
    fun rope(player: Player, target: GameObject, def: ObjectDefinition) {
        tieRope(player, def.stringId)
    }

    fun tieRope(player: Player, id: String) {
        if (!player.has(Skill.Agility, 70, message = true)) {
            return
        }
        if (!player.inventory.remove("rope")) {
            player.message("You aren't carrying a rope with you.")
            return
        }
        player.anim("climb_up")
        player[id.replace("rock", "rope")] = true
    }
}

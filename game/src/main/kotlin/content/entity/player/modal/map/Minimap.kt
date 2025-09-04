package content.entity.player.modal.map

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Open

class Minimap {

    @Open("health_orb")
    fun health(player: Player) {
        player["life_points"] = player.levels.get(Skill.Constitution)
        player.sendVariable("poisoned")
    }

    @Open("summoning_orb")
    fun summoning(player: Player) {
        player.sendVariable("show_summoning_orb")
    }
}

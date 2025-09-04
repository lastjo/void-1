package content.entity.player.dialogue

import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.type.sub.Interface

class ExperienceLamp {

    @Interface("Select", id = "skill_stat_advance")
    fun select(player: Player, component: String) {
        player["stat_advance_selected_skill"] = component
    }

    @Interface("Confirm", id = "skill_stat_advance")
    fun confirm(player: Player) {
        (player.dialogueSuspension as? StringSuspension)?.resume(player["stat_advance_selected_skill", "none"])
    }
}

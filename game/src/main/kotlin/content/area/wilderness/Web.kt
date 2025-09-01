package content.area.wilderness

import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn
import java.util.concurrent.TimeUnit

class Web {

    @Option("Slash", "web*")
    fun slashWeb(player: Player, target: GameObject) {
        if (player.weapon.def["slash_attack", 0] <= 0) {
            player.message("Only a sharp blade can cut through this sticky web.")
            return
        }
        slash(player, target)
    }

    @UseOn(on = "web*")
    fun use(player: Player, target: GameObject, item: Item) {
        if (item.id == "knife" || item.def["slash_attack", 0] > 0) {
            player.message("Only a sharp blade can cut through this sticky web.")
            return
        }
        slash(player, target)
    }

    private fun slash(player: Player, target: GameObject) {
        player.anim("dagger_slash")
        target.replace("web_slashed", ticks = TimeUnit.MINUTES.toTicks(1))
    }
}

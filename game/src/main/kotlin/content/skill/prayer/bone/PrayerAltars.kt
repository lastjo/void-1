package content.skill.prayer.bone

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Option

class PrayerAltars {

    @Option("Pray", "prayer_altar_*")
    @Option("Pray-at", "prayer_altar_*")
    fun pray(player: Player, target: GameObject) {
        if (player.levels.getOffset(Skill.Prayer) >= 0) {
            player.message("You already have full Prayer points.")
        } else {
            player.levels.set(Skill.Prayer, player.levels.getMax(Skill.Prayer))
            player.anim("altar_pray")
            player.message("You recharge your Prayer points.")
            player["prayer_point_power_task"] = true
        }
    }

    @Option("Check", "prayer_altar_chaos_varrock")
    fun check(player: Player, target: GameObject) {
        player.message("An altar to the evil god Zamorak.")
    }
}

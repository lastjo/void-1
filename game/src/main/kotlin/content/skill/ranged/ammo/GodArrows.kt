package content.skill.ranged.ammo

import content.entity.combat.hit.Damage
import content.entity.combat.hit.hit
import content.skill.ranged.ammo
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.timer.CLIENT_TICKS
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class GodArrows {

    @Combat(type = "range")
    fun combat(player: Player, target: Character, weapon: Item, type: String, delay: Int) {
        val bow = when (player.ammo) {
            "saradomin_arrows" -> "saradomin_bow"
            "guthix_arrows" -> "guthix_bow"
            "zamorak_arrows" -> "zamorak_bow"
            else -> return
        }
        val chance = if (weapon.id == bow) 0.2 else 0.1
        if (random.nextDouble() < chance) {
            // water_strike
            val damage = Damage.roll(player, target, type, weapon)
            player.hit(target, weapon, "magic", CLIENT_TICKS.toTicks(delay), damage = damage)
        }
    }
}

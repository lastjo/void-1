package content.skill.prayer.active

import content.entity.combat.hit.hit
import content.skill.prayer.Prayer
import world.gregs.voidps.engine.data.definition.PrayerDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.PrayerStart
import world.gregs.voidps.type.sub.PrayerStop

class PrayerBonus(private val definitions: PrayerDefinitions) {

    @PrayerStart
    fun pray(player: Player, id: String) {
        val definition = definitions.getOrNull(id) ?: return
        for ((bonus, value) in definition.bonuses) {
            player["base_${bonus}_bonus"] = player["base_${bonus}_bonus", 1.0] + value / 100.0
        }
    }

    @PrayerStop
    fun stop(player: Player, id: String) {
        val definition = definitions.getOrNull(id) ?: return
        for ((bonus, value) in definition.bonuses) {
            player["base_${bonus}_bonus"] = player["base_${bonus}_bonus", 1.0] - value / 100.0
        }
    }

    @Combat(stage = CombatStage.ATTACK)
    fun attack(source: Character, target: Character, type: String, delay: Int) {
        if (!Prayer.usingDeflectPrayer(source, target, type)) {
            return
        }
        val damage = target["protected_damage", 0]
        if (damage > 0) {
            target.anim("deflect", delay)
            target.gfx("deflect_$type", delay)
            if (random.nextDouble() >= 0.4) {
                target.hit(target = source, offensiveType = "deflect", delay = delay, damage = (damage * 0.10).toInt())
            }
        }
    }
}

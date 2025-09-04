package content.skill.ranged.weapon

import content.area.wilderness.inMultiCombat
import content.entity.combat.hit.directHit
import content.entity.sound.sound
import content.skill.melee.weapon.multiTargets
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import kotlin.random.nextInt

class Chinchompa {

    @Combat("*chinchompa", "range", stage = CombatStage.DAMAGE)
    fun damage(player: Character, source: Player) {
        source.sound("chinchompa_explode", delay = 40)
        player.gfx("chinchompa_impact")
    }

    @Combat(type = "range")
    fun combat(player: Player, target: Character, weapon: Item, damage: Int, type: String, spell: String) {
        if (weapon.id.endsWith("chinchompa") && target.inMultiCombat) {
            val targets = multiTargets(target, if (target is Player) 9 else 11)
            for (targ in targets) {
                targ.directHit(player, random.nextInt(0..damage), type, weapon, spell)
            }
        }
    }
}

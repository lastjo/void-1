package content.skill.magic.spell

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

class Spells {

    @Combat(stage = CombatStage.DAMAGE)
    fun hit(source: Character, target: Character, spell: String) {
        if (spell.isNotBlank()) {
            target.gfx("${spell}_impact")
            target.sound("${spell}_impact")
            source.sound("${spell}_impact")
        }
    }

    @Combat(type = "magic")
    fun magic(player: Player, target: Character, spell: String, type: String, weapon: Item, damage: Int) {
        if (!target.inMultiCombat) {
            return
        }
        if (spell.endsWith("_burst") || spell.endsWith("_barrage")) {
            val targets = multiTargets(target, 9)
            for (target in targets) {
                target.directHit(player, random.nextInt(0..damage), type, weapon, spell)
            }
        }
    }
}

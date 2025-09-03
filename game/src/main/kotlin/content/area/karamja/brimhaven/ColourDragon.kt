package content.area.karamja.brimhaven

import content.entity.combat.CombatSwing
import content.entity.combat.hit.hit
import content.entity.combat.npcCombatSwing
import content.entity.sound.sound
import kotlinx.coroutines.delay
import world.gregs.voidps.engine.entity.character.mode.move.target.CharacterTargetStrategy
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class ColourDragon {

    @Combat(id = "blue_dragon", stage = CombatStage.SWING)
    @Combat(id = "black_dragon", stage = CombatStage.SWING)
    @Combat(id = "green_dragon", stage = CombatStage.SWING)
    @Combat(id = "red_dragon", stage = CombatStage.SWING)
    suspend fun swing(npc: NPC, target: Player) {
        val withinMelee = CharacterTargetStrategy(npc).reached(target)
        if (!withinMelee) {
            delay(1)
        }
        val useFire = random.nextInt(4) == 0 // 1 in 4 chance to breathe fire
        if (useFire) {
            npc.anim("colour_dragon_breath")
            npc.gfx("dragon_breath_shoot")
            npc.hit(target, offensiveType = "dragonfire", special = true)
            target.sound("dragon_breath")
        } else {
            npc.anim("colour_dragon_attack")
            npc.hit(target, offensiveType = "melee")
            target.sound("dragon_attack")
        }
    }

}

package content.skill.ranged.weapon.special

import content.entity.combat.hit.hit
import content.entity.proj.shoot
import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.SpecialAttack

class Seercull {

    @SpecialAttack("soulshot")
    fun special(player: Player, target: Character) {
        player.anim("bow_accurate")
        player.gfx("seercull_special_shoot")
        player.sound("seercull_special")
        val time = player.shoot(id = "seercull_special_arrow", target = target)
        player.hit(target, delay = time)
    }

    @Combat("seercull", "range", stage = CombatStage.DAMAGE)
    fun damage(source: Character, target: Character) {
        source.gfx("seercull_special_impact")
    }

    @Combat("seercull*")
    fun combat(player: Player, target: Character, damage: Int, special: Boolean) {
        if (target["soulshot", false] || !special) {
            return
        }
        target["soulshot"] = true
        target.levels.drain(Skill.Magic, damage / 10)
    }

    @LevelChange(Skill.MAGIC)
    fun level(character: Character, skill: Skill, to: Int) {
        if (character["soulshot", false] && to >= character.levels.getMax(skill)) {
            character.clear("soulshot")
        }
    }
}

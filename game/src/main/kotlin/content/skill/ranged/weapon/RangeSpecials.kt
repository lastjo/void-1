package content.skill.ranged.weapon

import content.entity.combat.hit.hit
import content.entity.proj.shoot
import content.entity.sound.sound
import content.skill.ranged.ammo
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.distanceTo
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.SpecialAttack

class RangeSpecials {


    @SpecialAttack("snipe")
    fun dorgeshuunCrossbow(player: Player, target: Character, id: String) {
        player.anim("crossbow_accurate")
        player.sound("${id}_special")
        val time = player.shoot(id = "snipe_special", target = target)
        player.hit(target, delay = time)
    }

    @SpecialAttack("defiance")
    fun zaniksCrossbow(player: Player, target: Character) {
        player.anim("zaniks_crossbow_special")
        player.gfx("zaniks_crossbow_special")
        val time = player.shoot(id = "zaniks_crossbow_bolt", target = target)
        val damage = player.hit(target, delay = time)
        if (damage != -1) {
            target.levels.drain(Skill.Defence, damage / 10)
        }
    }

    @Combat(type = "range")
    fun enchantedBolts(player: Player, target: Character, damage: Int) {
        if (!player.hasClock("life_leech") || damage < 4) {
            return
        }
        player.levels.restore(Skill.Constitution, damage / 4)
    }

    @SpecialAttack("powershot")
    fun magicLongbow(player: Player, target: Character, id: String) {
        player.anim("bow_accurate")
        player.gfx("special_arrow_shoot")
        player.sound("${id}_special")
        val time = player.shoot(id = "special_arrow", target = target)
        player.hit(target, delay = time)
    }

    @SpecialAttack("snapshot")
    fun magicShortBow(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.gfx("${id}_special", delay = 30)
        player.sound("${id}_special")
        val distance = player.tile.distanceTo(target)
        val time1 = player.shoot(id = "special_arrow", target = target, delay = 20, flightTime = 10 + distance * 3)
        val time2 = player.shoot(id = "special_arrow", target = target, delay = 50, flightTime = distance * 3)
        player.hit(target, delay = time1)
        player.hit(target, delay = time2)
    }

    @SpecialAttack("hamstring")
    fun morrigansThrowingAxe(player: Player, target: Character) {
        val ammo = player.ammo
        player.anim("throw_morrigans_throwing_axe_special")
        player.gfx("${ammo}_special")
        val time = player.shoot(id = ammo, target = target, height = 15)
        if (player.hit(target, delay = time) != -1) {
            target.start("hamstring", 100)
        }
    }

}

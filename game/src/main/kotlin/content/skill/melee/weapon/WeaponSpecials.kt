package content.skill.melee.weapon

import content.area.wilderness.inMultiCombat
import content.entity.combat.Target
import content.entity.combat.hit.Damage
import content.entity.combat.hit.Hit
import content.entity.combat.hit.hit
import content.entity.combat.target
import content.entity.effect.freeze
import content.entity.player.combat.special.specialAttack
import content.entity.player.effect.energy.runEnergy
import content.entity.sound.areaSound
import content.entity.sound.sound
import org.rsmod.game.pathfinder.LineValidator
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.update.view.Viewport.Companion.VIEW_RADIUS
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.CharacterSearch
import world.gregs.voidps.engine.entity.character.mode.move.hasLineOfSight
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.map.collision.blocked
import world.gregs.voidps.engine.map.spiral
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.SpecialAttack
import java.util.concurrent.TimeUnit
import kotlin.math.max

class WeaponSpecials(
    private val players: Players,
    private val npcs: NPCs,
    private val lineOfSight: LineValidator,
) {

    @SpecialAttack("energy_drain", damage = true)
    fun abyssalWhip(player: Player, target: Character) {
        if (target !is Player) {
            return
        }
        val tenPercent = (target.runEnergy / 100) * 10
        if (tenPercent > 0) {
            target.runEnergy -= tenPercent
            player.runEnergy += tenPercent
            target.message("You feel drained!")
        }
    }

    @SpecialAttack("favour_of_the_war_god", damage = true)
    fun ancientMace(player: Player, target: Character, damage: Int) {
        val drain = damage / 10
        if (drain > 0) {
            target.levels.drain(Skill.Prayer, drain)
            player.levels.restore(Skill.Prayer, drain)
        }
    }

    @SpecialAttack("ice_cleave", damage = true)
    fun zamorakGodSword(player: Player, target: Character) {
        player.freeze(target, TimeUnit.SECONDS.toTicks(20))
    }

    @SpecialAttack("warstrike", damage = true)
    fun bandosGodSword(player: Player, target: Character, damage: Int) {
        drainByDamage(target, damage, Skill.Defence, Skill.Strength, Skill.Prayer, Skill.Attack, Skill.Magic, Skill.Ranged)
    }

    @SpecialAttack("healing_blade", damage = true)
    fun saradominGodSword(player: Player, target: Character, damage: Int) {
        player.levels.restore(Skill.Constitution, max(100, damage / 20))
        player.levels.restore(Skill.Prayer, max(50, damage / 40))
    }

    @SpecialAttack("saradomins_lightning")
    fun saradominSword(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        areaSound("godwars_godsword_special_attack", player.tile)
        val weapon = player.weapon
        val damage = Damage.roll(player, target, "melee", weapon)
        player.hit(target, damage = damage)
        if (damage > 0) {
            target.gfx("saradomins_lightning_impact")
            areaSound("godwars_saradomin_magic_impact", target.tile, 10)
            player.hit(target, offensiveType = "magic")
        }
    }

    @SpecialAttack("sunder")
    fun barrelChestAnchor(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        val damage = player.hit(target, delay = 60)
        if (damage >= 0) {
            drainByDamage(target, damage, Skill.Defence, Skill.Strength, Skill.Prayer, Skill.Attack, Skill.Magic, Skill.Ranged)
        }
    }

    @SpecialAttack("backstab", damage = true)
    fun boneDagger(player: Player, target: Character, damage: Int) {
        drainByDamage(target, damage, Skill.Defence)
    }

    @SpecialAttack("brine_sabre")
    fun brineSabre(player: Player, target: Character): Boolean {
        if (player.tile.region.id != 11924) {
            player.message("You can only use this special attack under water.")
            return true
        }
        return false
    }

    @SpecialAttack("weaken", damage = true)
    fun darkLight(player: Player, target: Character) {
        val amount = if (Target.isDemon(target)) 0.10 else 0.05
        target.levels.drain(Skill.Attack, multiplier = amount)
        target.levels.drain(Skill.Strength, multiplier = amount)
        target.levels.drain(Skill.Defence, multiplier = amount)
    }

    @SpecialAttack("powerstab", damage = true)
    fun dragon2h(player: Player, target: Character) {
        if (!player.inMultiCombat) {
            return
        }
        val characters: CharacterSearch<*> = if (target is Player) players else npcs
        var remaining = if (target is Player) 2 else 14
        for (direction in Direction.reversed) {
            val tile = player.tile.add(direction)
            for (char in characters[tile]) {
                if (char == player || char == target || !char.inMultiCombat || !Target.attackable(player, char)) {
                    continue
                }
                player.hit(char)
                if (--remaining <= 0) {
                    return
                }
            }
        }
    }

    @SpecialAttack("rampage")
    fun dragonBattleaxe(player: Player, target: Character, id: String): Boolean {
        if (!content.entity.player.combat.special.SpecialAttack.drain(player)) {
            return true
        }
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        player.say("Raarrrrrgggggghhhhhhh!")
        player.levels.drain(Skill.Attack, multiplier = 0.10)
        player.levels.drain(Skill.Defence, multiplier = 0.10)
        player.levels.drain(Skill.Magic, multiplier = 0.10)
        player.levels.drain(Skill.Ranged, multiplier = 0.10)
        player.levels.boost(Skill.Strength, amount = 5, multiplier = 0.15)
        return true
    }

    @SpecialAttack("slice_and_dice")
    fun dragonClaws(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")

        val weapon = player.weapon
        var (hit1, hit2, hit3, hit4) = intArrayOf(0, 0, 0, 0)
        val maxHit = Damage.maximum(player, target, "melee", weapon)
        if (Hit.success(player, target, "melee", weapon, special = true)) {
            hit1 = random.nextInt(maxHit / 2, maxHit - 10)
            hit2 = hit1 / 2
            hit3 = hit2 / 2
            hit4 = hit3 + if (random.nextBoolean()) 10 else 0
        } else if (Hit.success(player, target, "melee", weapon, special = true)) {
            hit2 = random.nextDouble(maxHit * 0.375, maxHit * 0.875).toInt()
            hit3 = hit2 / 2
            hit4 = hit3 + if (random.nextBoolean()) 10 else 0
        } else if (Hit.success(player, target, "melee", weapon, special = true)) {
            hit3 = random.nextDouble(maxHit * 0.25, maxHit * 0.75).toInt()
            hit4 = hit3 + if (random.nextBoolean()) 10 else 0
        } else if (Hit.success(player, target, "melee", weapon, special = true)) {
            hit4 = random.nextDouble(maxHit * 0.25, maxHit * 1.25).toInt()
        } else {
            hit3 = if (random.nextBoolean()) 10 else 0
            hit4 = if (random.nextBoolean()) 10 else 0
        }

        player.hit(target, damage = hit1)
        player.hit(target, damage = hit2)
        player.hit(target, damage = hit3, delay = 30)
        player.hit(target, damage = hit4, delay = 30)
    }

    @SpecialAttack("puncture", damage = true)
    fun dragonDagger(player: Player, target: Character, damage: Int) {
        if (damage >= 0) {
            player.hit(target)
        }
    }

    @SpecialAttack("sweep")
    fun dragonHalberd(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        val dir = target.tile.delta(player.tile).toDirection()
        val firstTile = target.tile.add(if (dir.isDiagonal()) dir.horizontal() else dir.rotate(2))
        val secondTile = target.tile.add(if (dir.isDiagonal()) dir.vertical() else dir.rotate(-2))
        val list = mutableListOf<Character>()
        list.add(target)
        val set = if (target is Player) players else npcs
        val groups = set.filter { it != target && it.tile.within(player.tile, VIEW_RADIUS) }.groupBy { it.tile }
        list.addAll(groups.getOrDefault(target.tile, emptyList()))
        list.addAll(groups.getOrDefault(firstTile, emptyList()))
        list.addAll(groups.getOrDefault(secondTile, emptyList()))
        list.take(if (target is Player) 3 else 10).onEach {
            player.hit(it)
        }
        if (target.size > 1) {
            player["second_hit"] = true
            player.hit(target)
            player.clear("second_hit")
        }
    }

    @SpecialAttack("clobber", damage = true)
    fun dragonHatchet(player: Player, target: Character, damage: Int) {
        val drain = damage / 100
        if (drain > 0) {
            target.levels.drain(Skill.Defence, drain)
            target.levels.drain(Skill.Magic, drain)
        }
    }

    @SpecialAttack("quick_smash")
    fun graniteMaul(player: Player, id: String): Boolean {
        if (player.target == null) {
            return false
        }
        if (!content.entity.player.combat.special.SpecialAttack.drain(player)) {
            return true
        }
        val target = player.target ?: return true
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.hit(target)
        return true
    }

    @SpecialAttack("disrupt")
    fun korasi(player: Player, target: Character, id: String) {
        player["korasi_chain"] = mutableSetOf(target.index)
        player.anim("${id}_special")
        player.gfx("${id}_special")
        areaSound("godwars_saradomin_magic_impact", player.tile, 10)
        areaSound("godwars_godsword_special_attack", player.tile, 5)
        val maxHit = Damage.maximum(player, target, "melee", player.weapon)
        val hit = random.nextInt(maxHit / 2, (maxHit * 1.5).toInt())
        player.hit(target, damage = hit, offensiveType = "magic", delay = 0)
    }

    @Combat(weapon = "korasis_sword", stage = CombatStage.DAMAGE)
    fun korasi(source: Character, target: Character, damage: Int, weapon: Item, type: String, special: Boolean) {
        if (!special) {
            return
        }
        areaSound("godwars_saradomin_magic_impact", target.tile, 10)
        target.gfx("disrupt_impact")
        if (!target.inMultiCombat) {
            return
        }
        val chain: MutableSet<Int> = source["korasi_chain", mutableSetOf()]
        if (chain.size >= 3) {
            return
        }
        val characters = if (target is Player) players else npcs
        for (tile in target.tile.spiral(4)) {
            for (character in characters[tile]) {
                if (character == target || chain.contains(character.index) || !Target.attackable(source, character)) {
                    continue
                }
                if (!lineOfSight.hasLineOfSight(target, character)) {
                    continue
                }
                chain.add(character.index)
                val hit = damage / when (chain.size) {
                    2 -> 2
                    3 -> 4
                    else -> return
                }
                source.hit(character, damage = hit, weapon = weapon, offensiveType = type, special = true)
                return
            }
        }
    }

    @Combat(type = "melee", stage = CombatStage.PREPARE)
    fun spearShove(player: Player, target: Character): Boolean {
        if (!player.specialAttack || player.weapon.def["special", ""] != "shove") {
            return false
        }
        if (target.size > 1) {
            player.message("That creature is too large to knock back!")
            return true
        } else if (target.hasClock("movement_delay")) {
            player.message("That ${if (target is Player) "player" else "creature"} is already stunned!")
            return true
        }
        return false
    }

    @SpecialAttack("shove")
    fun shove(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        val duration = TimeUnit.SECONDS.toTicks(3)
        target.gfx("dragon_spear_stun")
        target.freeze(duration)
        player["delay"] = duration
        player.hit(target, damage = -1) // Hit with no damage so target can auto-retaliate
        val actual = player.tile
        val direction = target.tile.delta(actual).toDirection()
        val delta = direction.delta
        if (!target.blocked(direction)) {
            target.exactMove(delta, 30, direction.inverse())
        }
    }

    @SpecialAttack("smash", damage = true)
    fun statiusWarhammer(player: Player, target: Character) {
        target.levels.drain(Skill.Defence, multiplier = 0.30)
    }

    @SpecialAttack("spear_wall", damage = true)
    fun vestasSpear(player: Player, target: Character, id: String) {
        player.start(id, duration = 8)
        if (!player.inMultiCombat) {
            return
        }
        var remaining = 15
        val characters: CharacterSearch<*> = if (target is Player) players else npcs
        for (tile in player.tile.spiral(1)) {
            for (char in characters[tile]) {
                if (char == player || char == target || !char.inMultiCombat || !Target.attackable(player, char)) {
                    continue
                }
                player.hit(char)
                if (--remaining <= 0) {
                    return
                }
            }
        }
    }

}
package content.skill.constitution.drink

import content.entity.effect.toxin.antiPoison
import content.entity.player.effect.antifire
import content.entity.player.effect.superAntifire
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class DungeoneeringPotions {

    @Consume("weak_melee_potion")
    fun weakMelee(player: Player) {
        player.levels.boost(Skill.Attack, 2, 0.07)
        player.levels.boost(Skill.Strength, 2, 0.07)
    }

    @Consume("weak_magic_potion")
    fun weakMagic(player: Player) {
        player.levels.boost(Skill.Magic, 2, 0.07)
    }

    @Consume("weak_range_potion")
    fun weakRange(player: Player) {
        player.levels.boost(Skill.Ranged, 2, 0.07)
    }

    @Consume("weak_range_potion")
    fun weakDef(player: Player) {
        player.levels.boost(Skill.Defence, 2, 0.07)
    }

    @Consume("weak_stat_restore_potion")
    fun weakStat(player: Player) {
        Skill.all.filterNot { it == Skill.Constitution || it == Skill.Prayer }.forEach { skill ->
            player.levels.restore(skill, 5, 0.12)
        }
    }

    @Consume("antipoison_dungeoneering")
    fun dungeoneeringAnti(player: Player) {
        player.antiPoison(1)
    }

    @Consume("weak_cure_potion")
    fun weakCure(player: Player) {
        player.antiPoison(5)
        player.antifire(5)
    }

    @Consume("weak_rejuvenation_potion")
    fun weakRejuve(player: Player) {
        player.levels.restore(Skill.Prayer, 4, 0.08)
        player.levels.restore(Skill.Summoning, 4, 0.08)
    }

    @Consume("weak_gatherers_potion")
    fun weakGather(player: Player) {
        player.levels.boost(Skill.Woodcutting, 3, 0.02)
        player.levels.boost(Skill.Mining, 3, 0.02)
        player.levels.boost(Skill.Fishing, 3, 0.02)
    }

    @Consume("weak_artisans_potion")
    fun weakArtisan(player: Player) {
        player.levels.boost(Skill.Smithing, 3, 0.02)
        player.levels.boost(Skill.Crafting, 3, 0.02)
        player.levels.boost(Skill.Fletching, 3, 0.02)
        player.levels.boost(Skill.Construction, 3, 0.02)
        player.levels.boost(Skill.Firemaking, 3, 0.02)
    }

    @Consume("weak_naturalists_potion")
    fun weakNat(player: Player) {
        player.levels.boost(Skill.Cooking, 3, 0.02)
        player.levels.boost(Skill.Farming, 3, 0.02)
        player.levels.boost(Skill.Herblore, 3, 0.02)
        player.levels.boost(Skill.Runecrafting, 3, 0.02)
    }

    @Consume("weak_survivalists_potion")
    fun weakSurvive(player: Player) {
        player.levels.boost(Skill.Agility, 3, 0.02)
        player.levels.boost(Skill.Hunter, 3, 0.02)
        player.levels.boost(Skill.Thieving, 3, 0.02)
        player.levels.boost(Skill.Slayer, 3, 0.02)
    }

    @Consume("melee_potion_dungeoneering")
    fun melee(player: Player) {
        player.levels.boost(Skill.Attack, 3, 0.11)
        player.levels.boost(Skill.Strength, 3, 0.11)
    }

    @Consume("magic_potion_dungeoneering")
    fun magic(player: Player) {
        player.levels.boost(Skill.Magic, 3, 0.11)
    }

    @Consume("ranged_potion_dungeoneering")
    fun range(player: Player) {
        player.levels.boost(Skill.Ranged, 3, 0.11)
    }

    @Consume("defence_potion_dungeoneering")
    fun def(player: Player) {
        player.levels.boost(Skill.Defence, 3, 0.11)
    }

    @Consume("stat_restore_potion_dungeoneering")
    fun statRest(player: Player) {
        Skill.all.filterNot { it == Skill.Constitution || it == Skill.Prayer }.forEach { skill ->
            player.levels.restore(skill, 7, 0.17)
        }
    }

    @Consume("cure_potion")
    fun cure(player: Player) {
        player.antiPoison(10)
        player.superAntifire(10)
    }

    @Consume("rejuvenation_potion")
    fun rejuve(player: Player) {
        player.levels.restore(Skill.Prayer, 7, 0.15)
        player.levels.restore(Skill.Summoning, 7, 0.15)
    }

    @Consume("gatherers_potion")
    fun gather(player: Player) {
        player.levels.boost(Skill.Woodcutting, 4, 0.04)
        player.levels.boost(Skill.Mining, 4, 0.04)
        player.levels.boost(Skill.Fishing, 4, 0.04)
    }

    @Consume("artisans_potion")
    fun artisan(player: Player) {
        player.levels.boost(Skill.Smithing, 4, 0.04)
        player.levels.boost(Skill.Crafting, 4, 0.04)
        player.levels.boost(Skill.Fletching, 4, 0.04)
        player.levels.boost(Skill.Construction, 4, 0.04)
        player.levels.boost(Skill.Firemaking, 4, 0.04)
    }

    @Consume("naturalists_potion")
    fun natural(player: Player) {
        player.levels.boost(Skill.Cooking, 4, 0.04)
        player.levels.boost(Skill.Farming, 4, 0.04)
        player.levels.boost(Skill.Herblore, 4, 0.04)
        player.levels.boost(Skill.Runecrafting, 4, 0.04)
    }

    @Consume("survivalists_potion")
    fun survive(player: Player) {
        player.levels.boost(Skill.Agility, 4, 0.04)
        player.levels.boost(Skill.Hunter, 4, 0.04)
        player.levels.boost(Skill.Thieving, 4, 0.04)
        player.levels.boost(Skill.Slayer, 4, 0.04)
    }

    @Consume("strong_melee_potion")
    fun strongMelee(player: Player) {
        player.levels.boost(Skill.Attack, 6, 0.2)
        player.levels.boost(Skill.Strength, 6, 0.2)
    }

    @Consume("strong_magic_potion")
    fun strongMagic(player: Player) {
        player.levels.boost(Skill.Magic, 6, 0.2)
    }

    @Consume("strong_ranged_potion")
    fun strongRange(player: Player) {
        player.levels.boost(Skill.Ranged, 6, 0.2)
    }

    @Consume("strong_defence_potion")
    fun strongDef(player: Player) {
        player.levels.boost(Skill.Defence, 6, 0.2)
    }

    @Consume("strong_stat_restore_potion")
    fun strongRestore(player: Player) {
        Skill.all.filterNot { it == Skill.Constitution || it == Skill.Prayer }.forEach { skill ->
            player.levels.restore(skill, 10, 0.24)
        }
    }

    @Consume("strong_cure_potion")
    fun strongCure(player: Player) {
        player.antiPoison(20)
        player.superAntifire(20)
    }

    @Consume("strong_rejuvenation_potion")
    fun strongRejuve(player: Player) {
        player.levels.restore(Skill.Prayer, 10, 0.22)
        player.levels.restore(Skill.Summoning, 10, 0.22)
    }

    @Consume("strong_gatherers_potion")
    fun strongGather(player: Player) {
        player.levels.boost(Skill.Woodcutting, 6, 0.06)
        player.levels.boost(Skill.Mining, 6, 0.06)
        player.levels.boost(Skill.Fishing, 6, 0.06)
    }

    @Consume("strong_artisans_potion")
    fun strongArtisan(player: Player) {
        player.levels.boost(Skill.Smithing, 6, 0.06)
        player.levels.boost(Skill.Crafting, 6, 0.06)
        player.levels.boost(Skill.Fletching, 6, 0.06)
        player.levels.boost(Skill.Construction, 6, 0.06)
        player.levels.boost(Skill.Firemaking, 6, 0.06)
    }

    @Consume("strong_naturalists_potion")
    fun strongNatural(player: Player) {
        player.levels.boost(Skill.Cooking, 6, 0.06)
        player.levels.boost(Skill.Farming, 6, 0.06)
        player.levels.boost(Skill.Herblore, 6, 0.06)
        player.levels.boost(Skill.Runecrafting, 6, 0.06)
    }

    @Consume("strong_survivalists_potion")
    fun strongSurvive(player: Player) {
        player.levels.boost(Skill.Agility, 6, 0.06)
        player.levels.boost(Skill.Hunter, 6, 0.06)
        player.levels.boost(Skill.Thieving, 6, 0.06)
        player.levels.boost(Skill.Slayer, 6, 0.06)
    }

}

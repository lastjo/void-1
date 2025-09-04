package content.skill.ranged.weapon.special

import content.area.wilderness.inMultiCombat
import content.entity.combat.Target
import content.entity.combat.hit.hit
import content.entity.player.combat.special.SpecialAttack
import content.entity.proj.shoot
import content.skill.ranged.ammo
import org.rsmod.game.pathfinder.LineValidator
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.move.hasLineOfSight
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.map.spiral
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class RuneThrowingAxe(
    private val players: Players,
    private val npcs: NPCs,
    private val lineOfSight: LineValidator,
) {

    @world.gregs.voidps.type.sub.SpecialAttack("chainhit")
    fun special(player: Player, target: Character) {
        val ammo = player.ammo
        player["chain_hits"] = mutableSetOf(target.index)
        player.anim("rune_throwing_axe_special")
        player.gfx("${ammo}_special_throw")
        val time = player.shoot(id = "${ammo}_special", target = target)
        player.hit(target, delay = time)
    }

    @Combat("rune_throwing_axe", "range", stage = CombatStage.DAMAGE)
    fun combat(source: Character, target: Character, weapon: Item, type: String, special: Boolean) {
        if (source !is Player || !target.inMultiCombat || !special) {
            return
        }
        val chain: MutableSet<Int> = source.getOrPut("chain_hits") { mutableSetOf() }
        val characters = if (target is Player) players else npcs
        for (tile in target.tile.spiral(4)) {
            for (character in characters[tile]) {
                if (character == target || chain.contains(character.index) || !Target.attackable(source, character)) {
                    continue
                }
                if (!lineOfSight.hasLineOfSight(target, character)) {
                    continue
                }
                if (!SpecialAttack.drain(source)) {
                    source.clear("chain_hits")
                    return
                }
                chain.add(character.index)
                val time = target.shoot(id = "rune_throwing_axe_special", target = character)
                source.hit(character, weapon, type, special = true, delay = time)
                return
            }
        }
    }
}

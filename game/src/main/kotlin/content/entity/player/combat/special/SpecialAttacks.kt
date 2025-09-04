package content.entity.player.combat.special

import content.entity.combat.hit.hit
import content.entity.sound.sound
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.Variable

class SpecialAttacks {

    @Variable("special_attack", toBool = "true")
    fun set(player: Player, from: Any?) {
        if (from == true) {
            return
        }
        val id: String = player.weapon.def.getOrNull("special") ?: return
        if (Publishers.all.specialAttackPrepare(player, id)) {
            player.specialAttack = false
        }
    }

    @world.gregs.voidps.type.sub.SpecialAttack
    fun cancel(player: Player): Boolean = !SpecialAttack.hasEnergy(player)

    @world.gregs.voidps.type.sub.SpecialAttack(damage = true)
    fun attack(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        val damage = player.hit(target)
        if (damage >= 0) {
            target.gfx("${id}_impact")
        }
        Publishers.all.specialAttack(player, target, id, damage)
    }
}

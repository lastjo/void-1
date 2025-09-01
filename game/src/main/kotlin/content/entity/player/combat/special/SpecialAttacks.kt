package content.entity.player.combat.special

import content.entity.combat.hit.hit
import content.entity.sound.sound
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.client.variable.variableSet
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Subscribe
import world.gregs.voidps.type.sub.Variable

class SpecialAttacks {

    @Variable("special_attack", toBool = "true")
    fun set(player: Player, from: Any?) {
        if (from == true) {
            return
        }
        val id: String = player.weapon.def.getOrNull("special") ?: return
        val prepare = SpecialAttackPrepare(id)
        player.emit(prepare)
        if (prepare.cancelled) {
            player.specialAttack = false
        }
    }

    @world.gregs.voidps.type.sub.SpecialAttack(prepare = true)
    fun cancel(player: Player, target: Character): Boolean {
        return !SpecialAttack.hasEnergy(player)
    }

    @world.gregs.voidps.type.sub.SpecialAttack
    fun attack(player: Player, target: Character, id: String) {
        player.anim("${id}_special")
        player.gfx("${id}_special")
        player.sound("${id}_special")
        val damage = player.hit(target)
        if (damage >= 0) {
            target.gfx("${id}_impact")
        }
        player.emit(SpecialAttackDamage(id, target, damage))
    }

}

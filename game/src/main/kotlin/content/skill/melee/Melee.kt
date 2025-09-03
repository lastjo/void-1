package content.skill.melee

import content.entity.combat.hit.hit
import content.entity.player.combat.special.SpecialAttack
import content.entity.player.combat.special.specialAttack
import content.skill.melee.weapon.attackType
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.data.definition.WeaponAnimationDefinitions
import world.gregs.voidps.engine.data.definition.WeaponStyleDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class Melee(
    private val styleDefinitions: WeaponStyleDefinitions,
    private val animationDefinitions: WeaponAnimationDefinitions,
) {

    @Combat(type = "melee", stage = CombatStage.PREPARE)
    fun prepare(player: Player, target: Character): Boolean {
        return player.specialAttack && !SpecialAttack.hasEnergy(player)
    }

    @Combat(type = "melee", stage = CombatStage.SWING)
    fun swing(player: Player, target: Character) {
        if (player.specialAttack && SpecialAttack.drain(player)) {
            val id: String = player.weapon.def["special"]
            Publishers.all.specialAttack(player, target, id)
            return
        }
        val type: String? = player.weapon.def.getOrNull("weapon_type")
        val definition = if (type != null) animationDefinitions.get(type) else null
        var animation = definition?.attackTypes?.getOrDefault(player.attackType, definition.attackTypes["default"])
        if (animation == null) {
            val id = player.weapon.def["weapon_style", 0]
            val style = styleDefinitions.get(id)
            animation = "${style.stringId}_${player.attackType}"
        }
        player.anim(animation)
        player.hit(target)
    }

}

package content.skill.ranged

import content.entity.combat.hit.hit
import content.entity.npc.combat.NPCAttack
import content.entity.player.combat.special.SpecialAttack
import content.entity.player.combat.special.specialAttack
import content.entity.proj.shoot
import content.entity.sound.sound
import content.skill.melee.weapon.attackType
import content.skill.melee.weapon.weapon
import content.skill.slayer.categories
import world.gregs.voidps.engine.data.definition.AnimationDefinitions
import world.gregs.voidps.engine.data.definition.WeaponAnimationDefinitions
import world.gregs.voidps.engine.data.definition.WeaponStyleDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class Ranged(
    private val weaponStyles: WeaponStyleDefinitions,
    private val weaponDefinitions: WeaponAnimationDefinitions,
    private val animationDefinitions: AnimationDefinitions,
) {
    @Combat(type = "range", stage = CombatStage.PREPARE)
    fun prepare(player: Player, target: Character): Boolean = player.specialAttack && !SpecialAttack.hasEnergy(player)

    @Combat(type = "scorch", stage = CombatStage.SWING)
    @Combat(type = "range", stage = CombatStage.SWING)
    fun swing(source: Character, target: Character) {
        // TODO handle target sounds better
        var ammo = source.ammo
        val style = if (source is NPC) weaponStyles.get(source.def["weapon_style", "unarmed"]) else weaponStyles.get(source.weapon.def["weapon_style", 0])
        if (source is Player) {
            val required = Ammo.requiredAmount(source.weapon, source.specialAttack)
            if (source.specialAttack && SpecialAttack.drain(source)) {
                val id: String = source.weapon.def.getOrNull("special") ?: return
                Publishers.all.specialAttack(source, target, id)
                return
            }
            if (style.stringId != "sling") {
                Ammo.remove(source, target, ammo, required)
            }
        }
        if (style.stringId == "sling") {
            source.anim(ammo)
        }
        if (style.stringId == "crossbow") {
            ammo = if (ammo == "barbed_bolts" || ammo == "bone_bolts" || ammo == "hand_cannon_shot") ammo else "crossbow_bolt"
        } else if (style.stringId == "bow" && ammo.endsWith("brutal")) {
            ammo = "brutal_arrow"
        }
        var time = source.shoot(id = ammo, target = target)
        val weapon = source.weapon.id
        when (style.stringId) {
            "thrown" -> {
                val ammoName = source.ammo.removePrefix("corrupt_").removeSuffix("_p++").removeSuffix("_p+").removeSuffix("_p")
                source.gfx("${ammoName}_throw")
                if (weapon.contains("dart")) {
                    source.sound("dart_throw")
                } else if (weapon.contains("javelin")) {
                    source.sound("javelin_throw")
                } else if (weapon.contains("knife")) {
                    source.sound("knife_throw")
                } else if (weapon.contains("axe")) {
                    source.sound("axe_throw")
                } else if (source is Player) {
                    source.sound("thrown")
                }
            }
            "bow" -> {
                source.gfx("${if (ammo.endsWith("brutal")) "brutal" else ammo}_shoot")
                if (source is NPC) {
                    target.sound("${if (ammo.endsWith("brutal")) "brutal" else ammo}_shoot")
                } else {
                    if (weapon.contains("shortbow")) {
                        source.sound("shortbow_shoot")
                    } else {
                        source.sound("longbow_shoot")
                    }
                }
            }
            "fixed_device" -> {
                // TODO
            }
            "salamander" -> {
                time = 0
                source.gfx("salamander_${source.attackType}")
            }
        }
        val type = source.weapon.def.getOrNull("weapon_type") ?: style.stringId
        var animation: String?
        if (source is NPC && !source.categories.contains("human")) {
            animation = NPCAttack.anim(animationDefinitions, source, "attack")
        } else {
            val definition = weaponDefinitions.get(type)
            animation = definition.attackTypes.getOrDefault(source.attackType, definition.attackTypes["default"])
            if (animation == null) {
                animation = "${style.stringId}_${source.attackType}"
            }
        }
        source.anim(animation)
        source.hit(target, delay = if (time == -1) 64 else time)
    }
}

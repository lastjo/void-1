package content.entity.combat

import content.area.wilderness.inSingleCombat
import content.entity.player.combat.special.specialAttack
import content.skill.magic.spell.spell
import content.skill.melee.weapon.attackRange
import content.skill.melee.weapon.attackSpeed
import content.skill.melee.weapon.fightStyle
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.dialogue
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.PauseMode
import world.gregs.voidps.engine.entity.character.mode.combat.*
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.event.onEvent
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.Death
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Subscribe

class Combat {

    @Despawn
    fun despawn(character: Character) {
        for (attacker in character.attackers) {
            attacker.mode = EmptyMode
        }
    }

    @Death
    fun death(character: Character) {
        character.stop("in_combat")
        for (attacker in character.attackers) {
            if (attacker.target == character) {
                attacker.stop("in_combat")
            }
        }
    }

    @Combat(stage = CombatStage.DAMAGE)
    fun retaliate(source: Character, target: Character, type: String) {
        if (source == target || type == "poison" || type == "disease" || type == "healed") {
            return
        }
        if (target.mode !is CombatMovement && target.mode !is PauseMode) {
            retaliate(target, source)
        }
    }

    @Combat(stage = CombatStage.START)
    fun start(source: Character, target: Character) {
        if (target.inSingleCombat) {
            target.attackers.clear()
            target.attacker = source
        }
        target.attackers.add(source)
        retaliate(target, source)
    }

    @Combat(stage = CombatStage.STOP)
    fun stop(source: Character, target: Character) {
        if (target.dead) {
            source["face_entity"] = target
        } else {
            source.clearWatch()
        }
        source.target?.attackers?.remove(source)
        source.target = null
    }

    /**
     * CombatReached is emitted by [CombatMovement] every tick the [Character] is within range of the target
     */
    @Subscribe("combat_reached")
    fun reachedNPC(npc: NPC, id: Any?) {
        id as Character
        combat(npc, id)
    }

    @Subscribe("combat_reached")
    fun reachedPlayer(player: Player, id: Any?) {
        id as Character
        combat(player, id)
    }

    companion object {
        fun combat(character: Character, target: Character) {
            if (character.mode !is CombatMovement || character.target != target) {
                character.mode = CombatMovement(character, target)
                character.target = target
            }
            val movement = character.mode as CombatMovement
            if (character is Player && character.dialogue != null) {
                return
            }
            if (character.target == null || !Target.attackable(character, target)) {
                character.mode = EmptyMode
                return
            }
            val attackRange = character.attackRange
            if (!movement.arrived(if (attackRange == 1 && character.weapon.def["weapon_type", ""] != "salamander") -1 else attackRange)) {
                return
            }
            if (character.hasClock("action_delay")) {
                return
            }
            val prepare = CombatPrepare(target)
            character.emit(prepare)
            if (Publishers.all.combatAttack(character, target, character.fightStyle, -1, character.weapon, character.spell, stage = CombatStage.PREPARE)) {
                character.mode = EmptyMode
                return
            }
            if (character["debug", false] || target["debug", false]) {
                val player = if (character["debug", false] && character is Player) character else target as Player
                player.message("---- Swing (${character.identifier}) -> (${target.identifier}) -----")
            }
            if (!target.hasClock("in_combat")) {
                Publishers.all.combatAttack(character, target, character.fightStyle, -1, character.weapon, character.spell, stage = CombatStage.START)
                character.emit(CombatStart(target))
            }
            target.start("in_combat", 8)

            Publishers.all.combatAttack(character, target, character.fightStyle, -1, character.weapon, character.spell, stage = CombatStage.SWING)
            val swing = CombatSwing(target)
            character.emit(swing)
            (character as? Player)?.specialAttack = false
            var nextDelay = character.attackSpeed
            if (character.hasClock("miasmic") && (character.fightStyle == "range" || character.fightStyle == "melee")) {
                nextDelay *= 2
            }
            character.start("action_delay", nextDelay)
        }
    }

    fun retaliates(character: Character) = if (character is NPC) {
        character.def["retaliates", true]
    } else {
        character["auto_retaliate", false]
    }

    fun retaliate(character: Character, source: Character) {
        if (character.dead || character.levels.get(Skill.Constitution) <= 0 || !retaliates(character)) {
            return
        }
        if (character is Player && character.mode != EmptyMode) {
            return
        }
        if (character is NPC && character.mode is CombatMovement && character.hasClock("in_combat")) {
            return
        }
        character.mode = CombatMovement(character, source)
        character.target = source
        val delay = character.attackSpeed / 2
        character.start("action_delay", delay)
        character.start("in_combat", delay + 8)
    }
}

package content.entity.player.combat

import content.entity.player.dialogue.type.statement
import content.skill.magic.spell.spell
import content.skill.melee.weapon.attackRange
import content.skill.melee.weapon.fightStyle
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.mode.combat.CombatMovement
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class CombatAttack {

    @Option("Attack", approach = true)
    suspend fun attack(player: Player, target: NPC) {
        if (!player.has(Skill.Slayer, target.def["slayer_level", 0])) {
            player.message("You need a higher slayer level to know how to wound this monster.")
            return
        }
        if (player.equipped(EquipSlot.Weapon).id.endsWith("_greegree")) {
            player.dialogue { statement("You cannot attack as a monkey.") }
            return
        }
        if (target.id.endsWith("_dummy") && !handleCombatDummies(player, target)) {
            return
        }
        if (player.attackRange != 1) {
            player.approachRange(player.attackRange, update = false)
        } else {
            player.approachRange(null, update = true)
        }
        combatInteraction(player, target)
    }

    @Option("Attack")
    suspend fun operate(npc: NPC, target: NPC) {
        if (npc.attackRange != 1) {
            npc.approachRange(npc.attackRange, update = false)
        } else {
            npc.approachRange(null, update = true)
        }
        combatInteraction(npc, target)
    }

    @Option("Attack")
    suspend fun operate(character: Character, target: Player) {
        if (character.attackRange != 1) {
            character.approachRange(character.attackRange, update = false)
        } else {
            character.approachRange(null, update = true)
        }
        combatInteraction(character, target)
    }

    @UseOn(id = "*_spellbook")
    suspend fun use(player: Player, target: NPC, component: String) {
        if (!player.has(Skill.Slayer, target.def["slayer_level", 0])) {
            player.message("You need a higher slayer level to know how to wound this monster.")
            return
        }
        player.approachRange(8, update = false)
        player.spell = component
        if (target.id.endsWith("_dummy") && !handleCombatDummies(player, target)) {
            player.clear("spell")
            return
        }
        player["attack_speed"] = 5
        player["one_time"] = true
        player.attackRange = 8
        player.face(target)
        combatInteraction(player, target)
    }

    @Combat(stage = CombatStage.PREPARE)
    fun combat(player: Player, target: NPC) {
        if (player.contains("one_time")) {
            player.mode = EmptyMode
            player.clear("one_time")
        }
    }

    /**
     * Replaces the current interaction when combat is triggered via [Interact] to
     * allow the first [CombatStage.SWING] to occur on the same tick.
     * After [Interact] is complete it is switched to [CombatMovement]
     */
    fun combatInteraction(character: Character, target: Character) {
        val interact = character.mode as? Interact ?: return
        val block: suspend (Boolean) -> Unit = { content.entity.combat.Combat.combat(character, target) }
        interact.updateInteraction(block) { true }
    }

    suspend fun handleCombatDummies(player: Player, target: NPC): Boolean {
        val type = target.id.removeSuffix("_dummy")
        if (player.fightStyle == type) {
            return true
        }
        player.message("You can only use ${type.toTitleCase()} against this dummy.")
        player.approachRange(10, false)
        player.mode = EmptyMode
        return false
    }
}

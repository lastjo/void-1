package content.skill.magic.book.lunar

import content.area.wilderness.inMultiCombat
import content.entity.combat.hit.damage
import content.entity.player.combat.special.MAX_SPECIAL_ATTACK
import content.entity.player.combat.special.specialAttackEnergy
import content.entity.player.effect.energy.MAX_RUN_ENERGY
import content.entity.player.effect.energy.runEnergy
import content.entity.sound.sound
import content.skill.magic.spell.removeSpellItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.UseOn

class EnergyTransfer(private val definitions: SpellDefinitions) {

    @UseOn(id = "lunar_spellbook", component = "energy_transfer", approach = true)
    suspend fun other(player: Player, target: Player, component: String) {
        player.approachRange(2)
        if (target.specialAttackEnergy == MAX_SPECIAL_ATTACK) {
            player.message("This player has full special attack.")
            return
        }
        if (player.specialAttackEnergy != MAX_SPECIAL_ATTACK) {
            player.message("You must have 100% special attack energy to transfer.")
            return
        }
        if (player.levels.get(Skill.Constitution) < 100) {
            player.message("You need more hitpoints to cast this spell.")
            return
        }
        if (!target.inMultiCombat) {
            player.message("This player is not in a multi-combat zone.")
            return
        }
        if (!player["accept_aid", true]) {
            player.message("This player is not currently accepting aid.") // TODO proper message
            return
        }
        if (!player.removeSpellItems(component)) {
            return
        }
        val definition = definitions.get(component)
        player.start("movement_delay", 2)
        player.anim("lunar_cast")
        target.gfx(component)
        player.sound(component)
        player.experience.add(Skill.Magic, definition.experience)
        player.damage(random.nextInt(95, 100))
        player.specialAttackEnergy = 0
        target.specialAttackEnergy = MAX_SPECIAL_ATTACK
        target.runEnergy = MAX_RUN_ENERGY
    }

}

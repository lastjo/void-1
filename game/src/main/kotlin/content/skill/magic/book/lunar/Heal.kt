package content.skill.magic.book.lunar

import content.entity.combat.hit.damage
import content.entity.sound.sound
import content.skill.magic.spell.removeSpellItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.UseOn

class Heal(
    private val definitions: SpellDefinitions,
    private val players: Players,
) {

    @Interface("Cast", "heal_group", "lunar_spellbook")
    fun group(player: Player, component: String) {
        if (player.levels.get(Skill.Constitution) < player.levels.getMax(Skill.Constitution) * 0.11) {
            player.message("You don't have enough life points.")
            return
        }
        if (!player.removeSpellItems(component)) {
            return
        }
        val definition = definitions.get(component)
        var healed = 0
        val amount = (player.levels.get(Skill.Constitution) * 0.75).toInt() + 5
        player.anim("lunar_cast")
        player.sound(component)
        val group = players
            .filter { other -> other != player && other.tile.within(player.tile, 1) && other.levels.getOffset(Skill.Constitution) < 0 && player["accept_aid", true] }
            .take(5)
        group.forEach { target ->
            target.gfx(component)
            target.sound("heal_other_impact")
            player.experience.add(Skill.Magic, definition.experience)
            healed += target.levels.restore(Skill.Constitution, amount / group.size)
            target.message("You have been healed by ${player.name}.")
        }
        if (healed > 0) {
            player.damage(healed, delay = 2)
        }
    }

    @UseOn(id = "lunar_spellbook", component = "heal_other", approach = true)
    suspend fun other(player: Player, target: Player, component: String) {
        player.approachRange(2)
        if (target.levels.getOffset(Skill.Constitution) >= 0) {
            player.message("This player does not need healing.")
            return
        }
        if (player.levels.get(Skill.Constitution) < player.levels.getMax(Skill.Constitution) * 0.11) {
            player.message("You don't have enough life points.")
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
        val amount = (player.levels.get(Skill.Constitution) * 0.75).toInt() + 1
        player.start("movement_delay", 2)
        player.anim("lunar_cast")
        player.sound(component)
        target.gfx(component)
        target.sound("heal_other_impact")
        player.experience.add(Skill.Magic, definition.experience)
        val restored = target.levels.restore(Skill.Constitution, amount)
        target.message("You have been healed by ${player.name}.")
        player.damage(restored, delay = 2)
    }
}

package content.skill.magic.book.lunar

import content.entity.effect.toxin.curePoison
import content.entity.effect.toxin.poisoned
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

class Cure(
    private val definitions: SpellDefinitions,
    private val players: Players,
) {

    @Interface("Cast", "cure_group", "lunar_spellbook")
    fun group(player: Player, component: String) {
        if (!player.removeSpellItems(component)) {
            return
        }
        val definition = definitions.get(component)
        player.anim("lunar_cast_group")
        player.sound(component)
        player.experience.add(Skill.Magic, definition.experience)
        players
            .filter { other -> other.tile.within(player.tile, 1) && other.poisoned && player["accept_aid", true] }
            .forEach { target ->
                target.gfx(component)
                target.sound("cure_other_impact")
                target.curePoison()
                target.message("You have been cured by ${player.name}")
            }
    }

    @Interface("Cast", "cure_me", "lunar_spellbook")
    fun me(player: Player, component: String) {
        if (!player.poisoned) {
            player.message("You are not poisoned.")
            return
        }
        if (!player.removeSpellItems(component)) {
            return
        }
        val definition = definitions.get(component)
        player.anim("lunar_cast")
        player.gfx(component)
        player.sound(component)
        player.experience.add(Skill.Magic, definition.experience)
        player.curePoison()
    }

    @UseOn(id = "lunar_spellbook", component = "cure_other", approach = true)
    suspend fun other(player: Player, target: Player, component: String) {
        player.approachRange(2)
        if (!target.poisoned) {
            player.message("This player is not poisoned.")
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
        target.curePoison()
        target.sound("cure_other_impact")
        target.message("You have been cured by ${player.name}.")
    }
}

package content.skill.magic.book.modern

import content.entity.sound.sound
import content.skill.magic.spell.removeSpellItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.timer.TICKS
import world.gregs.voidps.type.sub.Interface

class Charge(private val definitions: SpellDefinitions) {

    @Interface("Cast", "charge", "modern_spellbook")
    fun cast(player: Player, component: String) {
        if (player.hasClock("charge_delay")) {
            val remaining = TICKS.toSeconds(player.remaining("charge_delay"))
            player.message("You must wait another $remaining ${"second".plural(remaining)} before casting this spell again.")
            return
        }
        if (!player.removeSpellItems(component)) {
            return
        }

        val definition = definitions.get(component)
        player.anim(component)
        player.sound(component)
        player.experience.add(Skill.Magic, definition.experience)
        player.start("charge", definition["effect_ticks"])
        player.start("charge_delay", definition["delay_ticks"])
    }
}

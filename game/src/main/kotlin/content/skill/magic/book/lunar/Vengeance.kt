package content.skill.magic.book.lunar

import content.entity.combat.hit.hit
import content.entity.sound.sound
import content.skill.magic.spell.removeSpellItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.data.definition.SpellDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.timer.epochSeconds
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.UseOn

class Vengeance(private val definitions: SpellDefinitions) {

    @Interface("Cast", "vengeance", "lunar_spellbook")
    fun cast(player: Player, component: String) {
        if (player.contains("vengeance")) {
            player.message("You already have vengeance cast.")
            return
        }
        if (player.remaining("vengeance_delay", epochSeconds()) > 0) {
            player.message("You can only cast vengeance spells once every 30 seconds.")
            return
        }
        if (!player.removeSpellItems(component)) {
            return
        }
        val definition = definitions.get(component)
        player.anim(component)
        player.gfx(component)
        player.sound(component)
        player.experience.add(Skill.Magic, definition.experience)
        player["vengeance"] = true
        player.start("vengeance_delay", definition["delay_seconds"], epochSeconds())
    }

    @UseOn(id = "lunar_spellbook", component = "energy_transfer", approach = true)
    suspend fun other(player: Player, target: Player, component: String) {
        player.approachRange(2)
        if (target.contains("vengeance")) {
            player.message("This player already has vengeance cast.")
            return
        }
        if (player.remaining("vengeance_delay", epochSeconds()) > 0) {
            player.message("You can only cast vengeance spells once every 30 seconds.")
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
        target["vengeance"] = true
        player.start("vengeance_delay", definition["delay_seconds"], epochSeconds())
    }

    @Combat(stage = CombatStage.DAMAGE)
    fun combat(player: Player, target: Character, type: String, damage: Int) {
        if (!player.contains("vengeance") || type == "damage" || damage < 4) {
            return
        }
        player.say("Taste vengeance!")
        player.hit(target = player, offensiveType = "damage", delay = 0, damage = (damage * 0.75).toInt())
        player.stop("vengeance")
    }
}

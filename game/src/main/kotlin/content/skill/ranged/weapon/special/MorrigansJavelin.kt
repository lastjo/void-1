package content.skill.ranged.weapon.special

import content.entity.combat.hit.directHit
import content.entity.combat.hit.hit
import content.entity.proj.shoot
import content.skill.ranged.ammo
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.SpecialAttack
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

class MorrigansJavelin {

    @SpecialAttack("phantom_strike")
    fun special(player: Player, target: Character) {
        val ammo = player.ammo
        player.anim("throw_javelin")
        player.gfx("${ammo}_special")
        val time = player.shoot(id = ammo, target = target)
        val damage = player.hit(target, delay = time)
        if (damage != -1) {
            target["phantom_damage"] = damage
            target["phantom"] = player
            target["phantom_first"] = "start"
            target.softTimers.start("phantom_strike")
        }
    }

    @TimerStart("phantom_strike")
    fun start(character: Character): Int = 3

    @TimerTick("phantom_strike")
    fun tick(character: Character): Int {
        val remaining = character["phantom_damage", 0]
        val damage = remaining.coerceAtMost(50)
        if (remaining - damage <= 0) {
            return TimerState.CANCEL
        }
        character["phantom_damage"] = remaining - damage
        val source = character["phantom", character]
        character.directHit(source, damage, "effect")
        (character as? Player)?.message("You ${character.remove("phantom_first") ?: "continue"} to bleed as a result of the javelin strike.")
        return TimerState.CONTINUE
    }

    @TimerStop("phantom_strike")
    fun stop(npc: NPC) {
        npc.clear("phantom")
        npc.clear("phantom_damage")
        npc.clear("phantom_first")
    }
}

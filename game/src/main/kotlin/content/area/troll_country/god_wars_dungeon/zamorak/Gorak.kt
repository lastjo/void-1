package content.area.troll_country.god_wars_dungeon.zamorak

import content.skill.prayer.protectMelee
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class Gorak {

    val skills = Skill.entries.toMutableSet().apply {
        remove(Skill.Constitution)
    }

    @Combat(id = "gorak*")
    fun combat(npc: NPC, target: Player, damage: Int) {
        if (damage > 0) {
            target.levels.drain(skills.random(random), random.nextInt(1, 4))
        }
    }

    @Combat(id = "gorak*", stage = CombatStage.DAMAGE)
    fun damage(npc: NPC, target: Player) {
        if (target.protectMelee()) {
            target.message("Your protective prayer doesn't seem to work!")
        }
    }

}

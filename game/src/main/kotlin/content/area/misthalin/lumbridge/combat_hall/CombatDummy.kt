package content.area.misthalin.lumbridge.combat_hall

import content.entity.combat.attackers
import content.skill.melee.weapon.fightStyle
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.LevelChange

class CombatDummy {

    @LevelChange(Skill.CONSTITUTION, id = "melee_dummy")
    @LevelChange(Skill.CONSTITUTION, id = "magic_dummy")
    fun change(npc: NPC, to: Int) {
        if (to > 10) {
            return
        }
        npc.levels.clear()
        for (attacker in npc.attackers) {
            attacker.mode = EmptyMode
        }
    }

    @Combat(stage = CombatStage.PREPARE)
    fun prepare(player: Player, target: Character): Boolean {
        if (target is NPC && target.id == "magic_dummy" && player.fightStyle != "magic") { // TODO use type?
            player.message("You can only use Magic against this dummy.")
            return true
        } else if (target is NPC && target.id == "melee_dummy" && player.fightStyle != "melee") {
            player.message("You can only use Melee against this dummy.")
            return true
        }
        return false
    }
}

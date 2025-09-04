package content.area.morytania.slayer_tower

import content.entity.combat.attacker
import content.entity.combat.attackers
import content.entity.combat.hit.damage
import content.entity.combat.inCombat
import content.entity.effect.transform
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class Gargoyle {

    @LevelChange(Skill.CONSTITUTION, id = "gargoyle")
    fun change(npc: NPC, to: Int) {
        if (to > 90) {
            return
        }
        for (attacker in npc.attackers) {
            attacker.mode = EmptyMode
            if (attacker is Player && attacker["killing_blow", false]) {
                smash(attacker, npc)
                break
            }
        }
    }

    @Option("Smash", "gargoyle")
    fun operate(player: Player, target: NPC) {
        if (target.inCombat && target.attacker != player) {
            player.message("Someone else is fighting that.")
            return
        }
        smash(player, target)
    }

    @UseOn("rock_hammer", "gargoyle")
    fun use(player: Player, target: NPC) {
        if (target.inCombat && target.attacker != player) {
            player.message("Someone else is fighting that.")
            return
        }
        smash(player, target)
    }

    fun smash(player: Player, target: NPC) {
        if (!player.inventory.contains("rock_hammer")) {
            player.message("You need a rock hammer to smash the gargoyle.")
            return
        }
        val hitpoints = target.levels.get(Skill.Constitution)
        if (hitpoints >= 90) {
            player.message("The gargoyle isn't weak enough to be harmed by the hammer.")
            return
        }
        player.anim("axe_smash")
        target.transform("gargoyle_smashed")
        target.anim("gargoyle_smash")
        target.damage(hitpoints, source = player)
        player.message("You smash the gargoyle with the rock hammer and it shatters into pieces.")
    }
}

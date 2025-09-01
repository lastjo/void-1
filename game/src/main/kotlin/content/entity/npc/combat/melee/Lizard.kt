package content.entity.npc.combat.melee

import content.entity.combat.attackers
import content.entity.combat.hit.damage
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.ItemOnNPC
import world.gregs.voidps.engine.client.ui.interact.itemOnNPCOperate
import world.gregs.voidps.engine.entity.character.mode.EmptyMode
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.CurrentLevelChanged
import world.gregs.voidps.engine.entity.character.player.skill.level.npcLevelChange
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.LevelChange
import world.gregs.voidps.type.sub.UseOn

class Lizard {

    @LevelChange(Skill.CONSTITUTION, "lizard")
    @LevelChange(Skill.CONSTITUTION, "small_lizard*")
    @LevelChange(Skill.CONSTITUTION, "desert_lizard*")
    fun change(npc: NPC, to: Int) {
        if (to > 10) {
            return
        }
        for (attacker in npc.attackers) {
            attacker.mode = EmptyMode
            if (attacker is Player && attacker["killing_blow", false]) {
                // TODO message
                iceCooler(attacker, npc)
            }
        }
    }

    @UseOn("ice_cooler", "lizard")
    @UseOn("ice_cooler", "small_lizard*")
    @UseOn("ice_cooler", "desert_lizard*")
    fun use(player: Player, target: NPC) {
        iceCooler(player, target)
    }

    fun iceCooler(player: Player, target: NPC) {
        if (!player.inventory.remove("ice_cooler")) {
            return
        }
        player.anim("pie_accurate")
        target.gfx("skip_water_splash")
        val hitpoints = target.levels.get(Skill.Constitution)
        if (hitpoints >= target.levels.getMax(Skill.Constitution) / 4) {
            player.message("The lizard isn't weak enough to be affected by the icy water.")
            return
        }
        player.message("The lizard shudders and collapses from the freezing water.")
        target.damage(hitpoints, source = player)
    }
}

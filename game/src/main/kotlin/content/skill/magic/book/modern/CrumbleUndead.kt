package content.skill.magic.book.modern

import content.skill.slayer.undead
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat

class CrumbleUndead {

    @Combat(type = "magic", spell = "crumble_undead", stage = CombatStage.PREPARE)
    fun prepare(player: Player, target: NPC): Boolean {
        if (!target.undead) {
            player.clear("autocast")
            player.message("This spell only affects skeletons, zombies, ghosts and shades")
            return true
        }
        return false
    }

}

package content.area.wilderness.abyss

import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.map.collision.random
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class AbyssalDemon {

    @Combat(id = "abyssal_demon", stage = CombatStage.DAMAGE)
    fun combat(npc: NPC, source: Player) {
        if (random.nextInt(6) == 0) {
            val tile = source.tile.toCuboid(1).random(npc) ?: return
            npc.tele(tile, clearMode = false)
            npc.anim("abyssal_demon_teleport")
            npc.gfx("abyssal_demon_teleport")
            npc.sound("abyssal_demon_teleport")
        } else if (random.nextInt(3) == 0) {
            val tile = npc.tile.toCuboid(1).random(npc) ?: return
            source.tele(tile)
            source.gfx("abyssal_demon_teleport")
        }
    }

}

package content.area.troll_country.god_wars_dungeon

import content.entity.combat.hit.hit
import content.entity.proj.shoot
import content.entity.sound.areaSound
import content.entity.sound.sound
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat

class ThrowerTroll {

    @Combat(id = "thrower_troll_trollheim*", stage = CombatStage.SWING)
    fun swing(npc: NPC, target: Player) {
        if (random.nextInt(10) == 0) {
            npc.say("Urg!")
        }
        areaSound("thrower_troll_attack", npc.tile, radius = 10)
        npc.anim("thrower_troll_attack")
        npc.shoot("troll_rock", target)
        npc.hit(target, offensiveType = "range")
    }

    @Combat(id = "troll_rock", type = "range", stage = CombatStage.DAMAGE)
    fun hit(npc: NPC, target: Player) {
        // TODO need range gfx field
        //  Could potentially rename `type` and have type as the spell/ammo?
        //      TODO Combat vs attack style
        target.sound("troll_rock_defend")
    }

}

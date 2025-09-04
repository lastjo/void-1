package content.area.troll_country.god_wars_dungeon.zamorak

import content.entity.combat.hit.hit
import content.entity.effect.toxin.poison
import content.entity.sound.areaSound
import content.entity.sound.sound
import content.skill.prayer.protectMelee
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Spawn

class KrilTsutsaroth(private val npcs: NPCs) {

    var kreeyath: NPC? = null
    var karlak: NPC? = null
    var gritch: NPC? = null

    @Combat(id = "kril_tsutsaroth", stage = CombatStage.SWING)
    fun swing(npc: NPC, target: Player) {
        when (random.nextInt(3)) {
            0 -> { // Magic
                target.sound("kril_tsutsaroth_magic", delay = 30)
                npc.anim("kril_tsutsaroth_magic_attack")
                npc.gfx("kril_tsutsaroth_magic_attack")
                //            npc.shoot("1211", target)
                npc.hit(target, offensiveType = "magic", delay = 1)
            }
            else -> { // Melee
                npc.anim("kril_tsutsaroth_attack")
                target.sound("kril_tsutsaroth_attack")
                if (random.nextInt(4) == 0) {
                    npc.poison(target, 80)
                }
                val slam = target is Player && random.nextInt(3) != 0 && target.protectMelee() && !target.hasClock("gwd_block_counter")
                if (slam) {
                    target.start("gwd_block_counter", random.nextInt(5) + 6)
                    target.levels.drain(Skill.Prayer, multiplier = 0.5)
                    target.message("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.")
                    npc.say("YARRRRRRR!")
                    //                areaSound("3274", npc.tile, radius = 15)
                    npc.hit(target, offensiveType = "damage", damage = 350 + (random.nextInt(15) * 10)) // TODO prayer mod?
                } else {
                    npc.hit(target, offensiveType = "melee")
                }
            }
        }
    }

    @Combat(id = "kril_tsutsaroth", type = "magic")
    fun hit(npc: NPC, target: Player, damage: Int) {
        if (damage > 0) {
            areaSound("kril_tsutsaroth_magic_impact", target.tile, radius = 15)
        } else {
            target.gfx("giant_splash")
            target.sound("spell_splash")
        }
    }

    @Spawn("kril_tsutsaroth")
    fun spawn(npc: NPC) {
        if (kreeyath == null) {
            kreeyath = npcs.add("balfrug_kreeyath", Tile(2921, 5319, 2))
        }
        if (karlak == null) {
            karlak = npcs.add("tstanon_karlak", Tile(2932, 5328, 2))
        }
        if (gritch == null) {
            gritch = npcs.add("zakln_gritch", Tile(2919, 5327, 2))
        }
    }

    @Despawn("balfrug_kreeyath")
    fun kreeyath(npc: NPC) {
        kreeyath = null
    }

    @Despawn("tstanon_karlak")
    fun karlak(npc: NPC) {
        karlak = null
    }

    @Despawn("zakln_gritch")
    fun gritch(npc: NPC) {
        gritch = null
    }
}

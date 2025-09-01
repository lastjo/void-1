package content.area.troll_country.god_wars_dungeon

import content.entity.combat.hit.directHit
import content.entity.player.effect.energy.runEnergy
import content.entity.sound.sound
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.mode.move.enterArea
import world.gregs.voidps.engine.entity.character.mode.move.exitArea
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerStop
import world.gregs.voidps.engine.timer.timerTick
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.*

class WindChill(private val areas: AreaDefinitions) {

    @Enter("godwars_chill_area")
    fun enter(player: Player) {
        player.sendVariable("godwars_knights_notes")
        player.timers.start("windchill")
    }

    @Exit("godwars_chill_area")
    fun exit(player: Player) {
        if (player.inventory.contains("knights_notes") || player.inventory.contains("knights_notes_opened")) {
            player["godwars_knights_notes"] = true
        }
        player.timers.stop("windchill")
    }

    @TimerStart("windchill")
    fun start(player: Player): Int {
        player.open("snow_flakes")
        return 10
    }

    @TimerTick("windchill")
    fun tick(player: Player): Int {
        if (player.tile !in areas["godwars_chill_area"]) {
            return 0
        }
        player.sound("windy")
        player.runEnergy = 0
        for (skill in Skill.all) {
            if (skill == Skill.Constitution) {
                if (player.levels.get(Skill.Constitution) > 10) {
                    player.directHit(10)
                }
                continue
            }
            player.levels.drain(skill, 1)
        }
        return -1
    }

    @TimerStop("windchill")
    fun stop(player: Player) {
        player.close("snow_flakes")
    }

}

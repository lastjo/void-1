package content.entity.player.effect

import content.area.wilderness.inWilderness
import content.entity.combat.attackers
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.mode.combat.combatStart
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.appearance
import world.gregs.voidps.engine.entity.character.player.flagAppearance
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerStop
import world.gregs.voidps.engine.timer.timerTick
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.*
import java.util.concurrent.TimeUnit

val Player.skulled: Boolean get() = skullCounter > 0

var Player.skullCounter: Int
    get() = get("skull_duration", 0)
    set(value) = set("skull_duration", value)

fun Player.skull(minutes: Int = 10, type: Int = 0) {
    set("skull", type)
    skullCounter = TimeUnit.MINUTES.toTicks(minutes) / 50
    softTimers.start("skull")
}

fun Player.unskull() {
    clear("skull")
    skullCounter = 0
    softTimers.stop("skull")
}

class Skull {

    @Spawn
    fun spawn(player: Player) {
        if (player.skulled) {
            player.softTimers.restart("skull")
        }
    }

    @Combat(stage = CombatStage.START)
    fun combat(player: Player, target: Player) {
        if (player.inWilderness && !player.attackers.contains(target)) {
            player.skull()
        }
    }

    @TimerStart("skull")
    fun start(player: Player): Int {
        player.appearance.skull = player["skull", 0]
        player.flagAppearance()
        if (player.interfaces.contains("items_kept_on_death")) {
            player.open("items_kept_on_death", close = true)
        }
        return 50
    }

    @TimerTick("skull")
    fun tick(player: Player): Int {
        if (--player.skullCounter <= 0) {
            return TimerState.CANCEL
        }
        return TimerState.CONTINUE
    }

    @TimerStop("skull")
    fun stop(player: Player) {
        player.clear("skull")
        player.clear("skull_duration")
        player.appearance.skull = -1
        player.flagAppearance()
        if (player.interfaces.contains("items_kept_on_death")) {
            player.open("items_kept_on_death", close = true)
        }
    }

}

package content.entity.effect

import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.timer.characterTimerStart
import world.gregs.voidps.engine.timer.characterTimerStop
import world.gregs.voidps.engine.timer.characterTimerTick
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

class ColourOverlay {

    @TimerStart("colour_overlay")
    fun start(character: Character): Int {
        val overlay = character.visuals.colourOverlay
        return (overlay.delay + overlay.duration) / 30
    }

    @TimerTick("colour_overlay")
    fun tick(character: Character): Int {
        return 0
    }

    @TimerStop("colour_overlay")
    fun stop(character: Character) {
        character.visuals.colourOverlay.reset()
    }

}

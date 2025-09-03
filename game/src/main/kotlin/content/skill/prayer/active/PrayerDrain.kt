package content.skill.prayer.active

import content.entity.sound.sound
import content.skill.prayer.PrayerConfigs
import content.skill.prayer.getActivePrayerVarKey
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.BitwiseValues
import world.gregs.voidps.engine.data.definition.PrayerDefinitions
import world.gregs.voidps.engine.data.definition.VariableDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerStop
import world.gregs.voidps.engine.timer.timerTick
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick

class PrayerDrain(
    private val definitions: PrayerDefinitions,
    private val variableDefinitions: VariableDefinitions,
) {

    @TimerStart("prayer_drain")
    fun start(player: Player): Int = 1

    @TimerTick("prayer_drain")
    fun tick(player: Player): Int {
        val equipmentBonus = player["prayer", 0]
        var prayerDrainCounter = player["prayer_drain_counter", 0]

        prayerDrainCounter += getTotalDrainEffect(player)
        val prayerDrainResistance = 60 + (equipmentBonus * 2)
        while (prayerDrainCounter > prayerDrainResistance) {
            player.levels.drain(Skill.Prayer, 1)
            prayerDrainCounter -= prayerDrainResistance
            if (player.levels.get(Skill.Prayer) == 0) {
                player.sound("prayer_drain")
                player.message("You have run out of Prayer points; you can recharge at an altar.")
                player["prayer_drain_counter"] = prayerDrainCounter
                return TimerState.CANCEL
            }
        }
        player["prayer_drain_counter"] = prayerDrainCounter
        return TimerState.CONTINUE
    }

    @TimerStop("prayer_drain")
    fun stop(player: Player) {
        player.clear(player.getActivePrayerVarKey())
        player[PrayerConfigs.USING_QUICK_PRAYERS] = false
    }

    fun getTotalDrainEffect(player: Player): Int {
        val listKey = player.getActivePrayerVarKey()
        val variable = variableDefinitions.get(listKey)
        val values = (variable?.values as BitwiseValues).values
        var total = 0
        for (prayer in values) {
            if (player.containsVarbit(listKey, prayer)) {
                val definition = definitions.get(prayer as String)
                total += definition.drain
            }
        }
        return total
    }
}

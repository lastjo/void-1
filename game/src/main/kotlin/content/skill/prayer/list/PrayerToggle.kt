@file:Suppress("UNCHECKED_CAST")

package content.skill.prayer.list

import content.skill.prayer.PrayerConfigs.ACTIVE_CURSES
import content.skill.prayer.PrayerConfigs.ACTIVE_PRAYERS
import content.skill.prayer.PrayerStart
import content.skill.prayer.PrayerStop
import net.pearx.kasechange.toSnakeCase
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.client.variable.variableBitAdd
import world.gregs.voidps.engine.client.variable.variableBitRemove
import world.gregs.voidps.engine.client.variable.variableSet
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Script

@Script
class PrayerToggle {

    val publishers: Publishers by inject()

    init {
        variableSet("activated_*") { player ->
            player.closeInterfaces()
            val from = (from as? List<String>)?.toSet() ?: emptySet()
            val to = (to as? List<String>)?.toSet() ?: emptySet()
            for (prayer in from.subtract(to)) {
                publishers.prayerStopPlayer(player, prayer)
                publishers.prayerStopCharacter(player, prayer)
                player.emit(PrayerStop(prayer))
            }
            for (prayer in to.subtract(from)) {
                publishers.prayerStartPlayer(player, prayer)
                publishers.prayerStartCharacter(player, prayer)
                player.emit(PrayerStart(prayer))
            }
        }

        variableBitAdd(ACTIVE_PRAYERS, ACTIVE_CURSES) { player ->
            player.closeInterfaces()
            val id = (value as String).toSnakeCase()
            publishers.prayerStartPlayer(player, id)
            publishers.prayerStartCharacter(player, id)
            player.emit(PrayerStart(id))
        }

        variableBitRemove(ACTIVE_PRAYERS, ACTIVE_CURSES) { player ->
            player.closeInterfaces()
            val id = (value as String).toSnakeCase()
            publishers.prayerStopPlayer(player, id)
            publishers.prayerStopCharacter(player, id)
            player.emit(PrayerStop(id))
        }
    }
}

@file:Suppress("UNCHECKED_CAST")

package content.skill.prayer.list

import content.skill.prayer.PrayerConfigs.ACTIVE_CURSES
import content.skill.prayer.PrayerConfigs.ACTIVE_PRAYERS
import net.pearx.kasechange.toSnakeCase
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.sub.Variable
import world.gregs.voidps.type.sub.VariableBits

class PrayerToggle {

    @Variable("activated_*")
    fun set(player: Player, from: Any?, to: Any?) {
        player.closeInterfaces()
        val from = (from as? List<String>)?.toSet() ?: emptySet()
        val to = (to as? List<String>)?.toSet() ?: emptySet()
        for (prayer in from.subtract(to)) {
            Publishers.all.prayerStopPlayer(player, prayer)
            Publishers.all.prayerStopCharacter(player, prayer)
        }
        for (prayer in to.subtract(from)) {
            Publishers.all.prayerStartPlayer(player, prayer)
            Publishers.all.prayerStartCharacter(player, prayer)
        }
    }

    @VariableBits(ACTIVE_PRAYERS, ACTIVE_CURSES)
    fun added(player: Player, value: Any?) {
        player.closeInterfaces()
        val id = (value as String).toSnakeCase()
        Publishers.all.prayerStartPlayer(player, id)
        Publishers.all.prayerStartCharacter(player, id)
    }

    @VariableBits(ACTIVE_PRAYERS, ACTIVE_CURSES, added = false)
    fun removed(player: Player, value: Any?) {
        player.closeInterfaces()
        val id = (value as String).toSnakeCase()
        Publishers.all.prayerStopPlayer(player, id)
        Publishers.all.prayerStopCharacter(player, id)
    }

}

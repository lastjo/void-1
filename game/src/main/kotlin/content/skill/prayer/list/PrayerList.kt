package content.skill.prayer.list

import content.skill.prayer.PrayerConfigs.PRAYERS
import content.skill.prayer.PrayerConfigs.SELECTING_QUICK_PRAYERS
import content.skill.prayer.PrayerConfigs.USING_QUICK_PRAYERS
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Refresh

class PrayerList {

    @Open("prayer_orb")
    fun open(player: Player) {
        player.sendVariable(SELECTING_QUICK_PRAYERS)
        player.sendVariable(USING_QUICK_PRAYERS)
    }

    @Open("prayer_list")
    fun openList(player: Player) {
        player.sendVariable(PRAYERS)
    }

    @Refresh("prayer_list")
    fun refresh(player: Player, id: String) {
        val quickPrayers = player[SELECTING_QUICK_PRAYERS, false]
        if (quickPrayers) {
            player.interfaceOptions.unlockAll(id, "quick_prayers", 0..29)
        } else {
            player.interfaceOptions.unlockAll(id, "regular_prayers", 0..29)
        }
    }
}

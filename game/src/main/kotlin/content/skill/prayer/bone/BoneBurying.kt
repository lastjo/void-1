package content.skill.prayer.bone

import com.github.michaelbull.logging.InlineLogger
import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.queue.weakQueue
import world.gregs.voidps.type.sub.Inventory

class BoneBurying {

    val logger = InlineLogger()

    @Inventory("Bury")
    fun bury(player: Player, item: Item, itemSlot: Int) {
        if (!item.def.contains("prayer_xp")) {
            return
        }
        if (player.hasClock("bone_delay")) {
            return
        }
        val xp = item.def["prayer_xp", 0.0]
        if (xp <= 0.0) {
            logger.warn { "Missing bone xp: ${item.id}" }
            return
        }
        player.message("You dig a hole in the ground.", ChatType.Filter)
        if (!player.inventory.remove(itemSlot, item.id)) {
            return
        }
        player.start("bone_delay", 1)
        player.anim("bend_down")
        player.exp(Skill.Prayer, xp)
        player.sound("bury_bones")
        player["i_wonder_if_itll_sprout_task"] = true
        player.weakQueue("bury", 1, onCancel = null) {
            player.message("You bury the ${item.def.name.lowercase()}.", ChatType.Filter)
        }
    }
}

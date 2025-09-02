package content.skill.fletching

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnItem
import world.gregs.voidps.engine.data.definition.data.FletchBolts
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.type.sub.UseOn

class Bolts {

    @UseOn("feather", "*_bolts_unf")
    fun fletch(player: Player, fromItem: Item, toItem: Item) {
        val bolts: FletchBolts = toItem.def.getOrNull("fletch_bolts") ?: return

        if (!player.has(Skill.Fletching, bolts.level, true)) {
            return
        }

        val currentFeathers = player.inventory.count("feather")
        val currentBoltUnf = player.inventory.count(toItem.id)

        val actualAmount = minOf(currentFeathers, currentBoltUnf, 10)

        if (actualAmount < 1) {
            player.message("You don't have enough materials to fletch bolts.", ChatType.Game)
            return
        }

        val createdBolt: String = toItem.id.replace("_unf", "")
        val success = player.inventory.transaction {
            remove(toItem.id, actualAmount)
            remove("feather", actualAmount)
            add(createdBolt, actualAmount)
        }

        if (!success) {
            return
        }

        val totalExperience = bolts.xp * actualAmount
        player.experience.add(Skill.Fletching, totalExperience)
        player.message("You fletch $actualAmount bolts.", ChatType.Game)
    }

}

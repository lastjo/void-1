package content.skill.fletching

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.data.FletchDarts
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.type.sub.UseOn

class Darts {

    @UseOn("feather", "*_dart_tip")
    fun use(player: Player, fromItem: Item, toItem: Item) {
        val darts: FletchDarts = toItem.def.getOrNull("fletch_dart") ?: return

        if (!player.has(Skill.Fletching, darts.level, true)) {
            return
        }

        val currentFeathers = player.inventory.count("feather")
        val currentDartTips = player.inventory.count(toItem.id)

        val actualAmount = minOf(currentFeathers, currentDartTips, 10)

        if (actualAmount < 1) {
            player.message("You don't have enough materials to fletch bolts.", ChatType.Game)
            return
        }

        val createdDart: String = toItem.id.replace("_tip", "")
        val success = player.inventory.transaction {
            remove(toItem.id, actualAmount)
            remove("feather", actualAmount)
            add(createdDart, actualAmount)
        }

        if (!success) {
            return
        }

        val totalExperience = darts.xp * actualAmount
        player.experience.add(Skill.Fletching, totalExperience)
        player.message("You finish making $actualAmount darts.", ChatType.Game)
    }
}

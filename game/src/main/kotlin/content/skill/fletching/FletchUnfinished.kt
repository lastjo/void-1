package content.skill.fletching

import content.entity.player.dialogue.type.makeAmount
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnItem
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.data.Fletching
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.event.Script
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.engine.queue.weakQueue

@Script
class FletchUnfinished {

    val itemDefinitions: ItemDefinitions by inject()

    init {
        itemOnItem("knife", "*logs*") {
            val displayItems = toItem.def.extras?.get("fletchables") as? List<String> ?: return@itemOnItem
            it.weakQueue("fletching_make_dialog") {
                val (selected, _) = makeAmount(
                    displayItems,
                    type = "Make",
                    maximum = 27,
                    text = "What would you like to fletch?",
                )

                val itemToFletch: Fletching = itemDefinitions.get(selected).getOrNull("fletching_unf") ?: return@weakQueue
                if (!it.has(Skill.Fletching, itemToFletch.level, true)) {
                    return@weakQueue
                }

                fletchAll(it, selected, itemToFletch, toItem.id)
            }
        }
    }

    fun fletchAll(player: Player, addItem: String, addItemDef: Fletching, removeItem: String) {
        val logsCount = getTotalLogs(player, removeItem)
        if (logsCount <= 0) {
            player.message("You don't have any logs to fletch.", ChatType.Game)
            return
        }

        val notedId = getNotedId(removeItem)
        val success = player.inventory.transaction {
            when {
                player.inventory.contains(removeItem) -> remove(removeItem, logsCount)
                player.inventory.contains(notedId) -> remove(notedId, logsCount)
                else -> return@transaction
            }
            add(addItem, logsCount * addItemDef.makeAmount)
        }

        if (!success) {
            player.message("You don't have enough space in your inventory.", ChatType.Game)
            return
        }

        val itemCreated = getFletched(addItem)
        player.message("You carefully cut all your logs into $itemCreated.", ChatType.Game)
        player.experience.add(Skill.Fletching, addItemDef.xp * logsCount)
        player.anim(addItemDef.animation)
    }

    private fun getTotalLogs(player: Player, item: String): Int {
        val notedId = getNotedId(item)
        return player.inventory.count(item) + player.inventory.count(notedId)
    }

    private fun getNotedId(item: String): String {
        val def = itemDefinitions.get(item)
        val noted = def.noteId
        return if (noted != -1) itemDefinitions.get(noted).stringId else item
    }

    fun getFletched(itemName: String): String = when {
        itemName.contains("shortbow", ignoreCase = true) -> "Shortbows"
        itemName.contains("longbow", ignoreCase = true) -> "Longbows"
        itemName.contains("stock", ignoreCase = true) -> "Stocks"
        itemName.contains("shaft", ignoreCase = true) -> "Arrow shafts"
        else -> "items"
    }
}



package content.skill.firemaking

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.data.LightSources
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.ReplaceItem.replace
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class LightSource {

    @UseOn("tinderbox*", "oil_lamp_oil")
    @UseOn("tinderbox*", "candle_lantern_white")
    @UseOn("tinderbox*", "candle_lantern_black")
    @UseOn("tinderbox*", "oil_lantern_oil")
    @UseOn("tinderbox*", "bullseye_lantern_oil")
    @UseOn("tinderbox*", "sapphire_lantern_oil")
    @UseOn("tinderbox*", "mining_helmet")
    @UseOn("tinderbox*", "emerald_lantern")
    @UseOn("tinderbox*", "white_candle")
    @UseOn("tinderbox*", "black_candle")
    @UseOn("tinderbox*", "unlit_torch")
    fun light(player: Player, fromItem: Item, toItem: Item) {
        val needsFlame: LightSources = toItem.def.getOrNull("light_source") ?: return

        if (!player.has(Skill.Firemaking, needsFlame.level, true)) {
            return
        }
        player.inventory.transaction {
            replace(toItem.id, needsFlame.onceLit)
        }
        val litItem = determineLightSource(needsFlame.onceLit)
        player.message("You light the $litItem", ChatType.Game)
    }

    @Inventory("Extinguish")
    fun extinguish(player: Player, item: Item) {
        val source: LightSources = item.def.getOrNull("light_source") ?: return
        player.inventory.transaction {
            replace(item.id, source.onceExtinguish)
        }
        player.message("You extinguish the flame.", ChatType.Game)
    }

    fun determineLightSource(itemName: String): String = when {
        itemName.contains("lantern", ignoreCase = true) -> "lantern."
        itemName.contains("candle", ignoreCase = true) -> "candle."
        else -> "null"
    }
}

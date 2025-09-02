package content.skill.herblore

import content.entity.player.inv.inventoryOption
import world.gregs.voidps.engine.data.definition.data.Cleaning
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Inventory

class HerbCleaning {

    @Inventory("Clean")
    fun clean(player: Player, item: Item, itemSlot: Int) {
        val herb: Cleaning = item.def.getOrNull("cleaning") ?: return
        if (!player.has(Skill.Herblore, herb.level, true)) {
            return
        }

        if (player.inventory.replace(itemSlot, item.id, item.id.replace("grimy", "clean"))) {
            player.experience.add(Skill.Herblore, herb.xp)
        }
    }

}

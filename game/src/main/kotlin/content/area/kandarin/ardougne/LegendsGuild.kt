package content.area.kandarin.ardougne

import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.statement
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.client.ui.interact.itemOnObjectOperate
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.engine.suspend.SuspendableContext
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn

class LegendsGuild {

    @Option("Look", "legends_guild_totem_pole")
    suspend fun lookPole(player: Player, target: GameObject) = player.dialogue {
        if (player.inventory.contains("combat_bracelet") && player.inventory.replace("combat_bracelet", "combat_bracelet_4")) {
            combatBracelet(player)
        } else if (player.inventory.contains("skills_necklace") && player.inventory.replace("skills_necklace", "skills_necklace_4")) {
            skillsNecklace(player)
        } else {
            statement("This totem pole is truly awe inspiring. It depicts powerful Karamjan animals. It is very well carved and brings a sense of power and spiritual fulfilment to anyone who looks at it.")
            player.message("You don't have any jewellery that the totem can recharge.")
        }
    }

    @UseOn("combat_bracelet", "legends_guild_totem_pole")
    suspend fun rechargeCombat(player: Player, target: GameObject, item: Item, itemSlot: Int) = player.dialogue {
        if (player.inventory.replace(itemSlot, item.id, "combat_bracelet_4")) {
            combatBracelet(player)
        }
    }

    @UseOn("skills_necklace", "legends_guild_totem_pole")
    suspend fun rechargeSkills(player: Player, target: GameObject, item: Item, itemSlot: Int) = player.dialogue {
        if (player.inventory.replace(itemSlot, item.id, "skills_necklace_4")) {
            skillsNecklace(player)
        }
    }

    suspend fun Dialogue.combatBracelet(player: Player) {
        player.message("You touch the jewellery against the totem pole...")
        player.anim("bend_down")
        item("combat_bracelet", 300, "You feel a power emanating from the totem pole as it recharges your bracelet. You can now rub the bracelet to teleport and wear it to get information while on a Slayer assignment.")
    }

    suspend fun Dialogue.skillsNecklace(player: Player) {
        player.message("You touch the jewellery against the totem pole...")
        player.anim("bend_down")
        item("skills_necklace", 200, "You feel a power emanating from the totem pole as it recharges your necklace. You can now rub the necklace to teleport and wear it to get more caskets while big net Fishing.")
    }
}

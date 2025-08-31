package content.area.asgarnia.burthorpe

import content.entity.player.dialogue.type.item
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.sub.UseOn

class HeroesGuild {

    @UseOn("amulet_of_glory", "fountain_of_heroes")
    suspend fun recharge(player: Player, obj: GameObject, item: Item, itemSlot: Int) {
        if (player.inventory.replace(itemSlot, item.id, "amulet_of_glory_4")) {
            player.message("You dip the amulet in the fountain...")
            player.anim("bend_down")
            player.dialogue {
                item("amulet_of_glory", 300, "You feel a power emanating from the fountain as it recharges your amulet. You can now rub the amulet to teleport and wear it to get more gems whilst mining.")
            }
        }
    }
}

package content.quest.member.ghosts_ahoy

import content.entity.player.inv.inventoryItem
import content.skill.magic.spell.Teleport.Companion.teleport
import content.skill.magic.spell.teleportLand
import content.skill.magic.spell.teleportTakeOff
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.ItemOnObject
import world.gregs.voidps.engine.client.ui.interact.itemOnObjectOperate
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.Teleport
import world.gregs.voidps.type.sub.TeleportLand
import world.gregs.voidps.type.sub.UseOn

class Ectophial(private val objects: GameObjects) {

    @Inventory("Empty", "ectophial", "inventory")
    suspend fun empty(player: Player) {
        player.gfx("empty_ectophial")
        player.animDelay("empty_ectophial")
        player.delay(2)
        teleport(player, "ectophial_teleport", "ectophial")
    }

    @UseOn("ectophial_empty", "ectofuntus")
    fun use(player: Player, target: GameObject, item: Item, itemSlot: Int) {
        if (player.inventory.replace(itemSlot, item.id, "ectophial")) {
            player.anim("take")
            player.message("You refill the ectophial from the Ectofuntus.")
        }
    }

    @Teleport("ectophial")
    fun tele(player: Player): Int {
        player.anim("empty_ectophial")
        player.gfx("empty_ectophial")
        player.message("You empty the ectoplasm onto the ground around your feet...", ChatType.Filter)
        return 1
    }

    @TeleportLand("ectophial")
    fun land(player: Player) {
        player.message("... and the world changes around you.", ChatType.Filter)
        val ectofuntus = objects[Tile(3658, 3518), "ectofuntus"] ?: return
        val slot = player.inventory.indexOf("ectophial")
        player.mode = Interact(player, ectofuntus, ItemOnObject(player, ectofuntus, Item("ectophial_empty"), slot, "inventory"))
    }
}

package content.skill.magic.jewellery

import content.entity.player.dialogue.type.choice
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.type.sub.Inventory

class RingOfDuelling(areas: AreaDefinitions) {

    val duelArena = areas["duel_arena_teleport"]
    val castleWars = areas["castle_wars_teleport"]
    val mobilisingArmies = areas["mobilising_armies_teleport"]
    val fistOfGuthix = areas["fist_of_guthix_teleport"]

    @Inventory("Rub", "ring_of_duelling_*")
    suspend fun rub(player: Player, item: Item, inventory: String, itemSlot: Int) = player.dialogue {
        if (player.contains("delay")) {
            return@dialogue
        }
        choice("Where would you like to teleport to?") {
            option("Al Kharid Duel Arena.") {
                jewelleryTeleport(player, inventory, itemSlot, duelArena)
            }
            option("Castle Wars Arena.") {
                jewelleryTeleport(player, inventory, itemSlot, castleWars)
            }
            option("Mobilising Armies Command Centre.") {
                jewelleryTeleport(player, inventory, itemSlot, mobilisingArmies)
            }
            option("Fist of Guthix.") {
                jewelleryTeleport(player, inventory, itemSlot, fistOfGuthix)
            }
            option("Nowhere.")
        }
    }

    @Inventory(item = "ring_of_duelling_*", inventory = "worn_equipment")
    fun tele(player: Player, item: Item, option: String, inventory: String, itemSlot: Int) {
        if (player.contains("delay")) {
            return
        }
        val area = when (option) {
            "Duel Arena" -> duelArena
            "Castle Wars" -> castleWars
            "Mobilising Armies" -> mobilisingArmies
            "Fist of Guthix" -> fistOfGuthix
            else -> return
        }
        jewelleryTeleport(player, inventory, itemSlot, area)
    }
}

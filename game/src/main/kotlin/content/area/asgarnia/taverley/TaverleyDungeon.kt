package content.area.asgarnia.taverley

import content.entity.obj.door.enterDoor
import content.quest.quest
import net.pearx.kasechange.toLowerSpaceCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.noInterest
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.*
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.engine.queue.softQueue
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.UseOn
import java.util.concurrent.TimeUnit

class TaverleyDungeon(
    private val npcs: NPCs,
    private val objects: GameObjects,
) {

    val leftSpawn = Tile(2887, 9832)
    val rightSpawn = Tile(2887, 9829)

    @Option("Open", "door_taverley_1_closed", "door_taverley_2_closed")
    suspend fun enter(player: Player, target: GameObject) {
        if (player.tile.x >= 2889 || !spawn(player, leftSpawn) && !spawn(player, rightSpawn)) {
            player.enterDoor(target)
        }
    }

    @UseOn("raw_beef", "cauldron_of_thunder")
    @UseOn("raw_rat_meat", "cauldron_of_thunder")
    @UseOn("raw_bear_meat", "cauldron_of_thunder")
    @UseOn("raw_chicken", "cauldron_of_thunder")
    fun dip(player: Player, fromItem: Item) {
        val required = fromItem.id
        if (player.quest("druidic_ritual") == "cauldron") {
            if (player.inventory.replace(required, required.replace("raw_", "enchanted_"))) {
                player.message("You dip the ${required.toLowerSpaceCase()} in the cauldron.")
            }
        } else {
            player.noInterest()
        }
    }

    fun spawn(player: Player, tile: Tile): Boolean {
        val armour = objects.getLayer(tile, ObjectLayer.GROUND) ?: return false
        armour.remove(TimeUnit.MINUTES.toTicks(5))
        val suit = npcs.add("suit_of_armour", armour.tile)
        player.message("Suddenly the suit of armour comes to life!")
        //    suit.setAnimation("suit_of_armour_stand") TODO find animation
        suit.softQueue("despawn", TimeUnit.MINUTES.toTicks(5)) {
            World.queue("despawn_${suit.index}") {
                npcs.remove(suit)
            }
        }
        return true
    }
}

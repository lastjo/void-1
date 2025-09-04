package content.entity.player.modal.tab

import content.entity.effect.clearTransform
import content.entity.effect.movementDelay
import content.entity.effect.transform
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.queue.queue
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Inventory
import kotlin.random.Random

class Morphing {

    @Inventory("Wear", "easter_ring")
    fun easterRing(player: Player) {
        morph(player, "easter_egg_${Random.nextInt(0, 6)}")
    }

    @Inventory("Wear", "ring_of_stone")
    fun ringOfStone(player: Player, item: Item) {
        morph(player, item.id)
    }

    fun morph(player: Player, npc: String) {
        player.transform(npc)
        player.movementDelay = Int.MAX_VALUE
        player.softTimers.start("movement_delay")
        player.open("morph")
        player.queue("morph", onCancel = { unmorph(player) }) {
        }
    }

    @Interface("Ok", "unmorph", "morph")
    fun unmorph(player: Player) {
        player.queue.clear()
        player.clearTransform()
        player.movementDelay = 0
        player.close("morph")
    }
}

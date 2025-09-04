package content.area.misthalin.varrock.palace

import com.github.michaelbull.logging.InlineLogger
import content.entity.player.bank.ownsItem
import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.Shifty
import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.player
import content.entity.sound.sound
import content.quest.quest
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.replace
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.UseOn

class VarrockPalaceDrain {

    val logger = InlineLogger()

    @Option("Search", "varrock_palace_drain")
    suspend fun operate(player: Player, target: GameObject) = player.dialogue {
        player.anim("climb_down")
        if (player["demon_slayer_drain_dislodged", false] || player.ownsItem("silverlight_key_sir_prysin")) {
            player.message("Nothing interesting seems to have been dropped down here today.")
        } else if (player.quest("demon_slayer") == "unstarted") {
            player<Shifty>("This is the drainpipe running from the kitchen sink to the sewer. I can see a key just inside the drain.")
        } else {
            player<Neutral>("That must be the key Sir Prysin dropped.")
            player<Shifty>("I don't seem to be able to reach it. I wonder if I can dislodge it somehow. That way it may go down into the sewers.")
        }
    }

    @Spawn
    fun spawn(player: Player) {
        if (player["demon_slayer_drain_dislodged", false]) {
            player.sendVariable("demon_slayer_drain_dislodged")
        }
    }

    @UseOn("*of_water", "varrock_palace_drain")
    suspend fun use(player: Player, target: GameObject, item: Item, itemSlot: Int) {
        val replacement = when {
            item.id.startsWith("bucket_of") -> "bucket"
            item.id.startsWith("jug_of") -> "jug"
            item.id.startsWith("pot_of") -> "empty_pot"
            item.id.startsWith("bowl_of") -> "bowl"
            else -> return
        }
        if (!player.inventory.replace(itemSlot, item.id, replacement)) {
            logger.warn { "Issue emptying ${item.id} -> $replacement" }
            return
        }
        player["demon_slayer_drain_dislodged"] = true
        player.message("You pour the liquid down the drain.")
        player.anim("toss_water")
        player.gfx("toss_water")
        player.sound("demon_slayer_drain")
        player.sound("demon_slayer_key_fall")
        player.dialogue {
            if (player.quest("demon_slayer") == "key_hunt") {
                player<Happy>("OK, I think I've washed the key down into the sewer. I'd better go down and get it!")
            } else {
                player<Shifty>("I think that dislodged something from the drain. It's probably gone down to the sewers below.")
            }
        }
    }

    @Option("Take", "demon_slayer_rusty_key")
    suspend fun takeKey(player: Player, target: GameObject) = player.dialogue {
        if (player.inventory.add("silverlight_key_sir_prysin")) {
            player["demon_slayer_drain_dislodged"] = false
            item("silverlight_key_sir_prysin", 400, "You pick up an old rusty key.")
        }
    }
}

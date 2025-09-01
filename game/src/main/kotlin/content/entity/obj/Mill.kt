package content.entity.obj

import content.entity.player.bank.bank
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.type.player
import content.entity.sound.sound
import content.quest.quest
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.interact.itemOnObjectOperate
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inv.*
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.UseOn

class Mill {

    @Option("Operate", "hopper_controls")
    suspend fun operate(player: Player, target: GameObject) {
        if (player["flour_bin", 0] == 30) {
            player.message("The flour bin downstairs is full, I should empty it first.")
            return
        }
        player.anim("pull_hopper_controls")
        player.sound("lever")
        player.delay()
        target.replace("hopper_controls_pulled", ticks = 2)
        if (player["hopper_bin", 0] != 1) {
            player.message("You operate the empty hopper. Nothing interesting happens.")
            return
        }
        player["hopper_bin"] = 0
        player.inc("flour_bin")
        if (player["flour_bin", 0] == 30) {
            player.message("The flour bin downstairs is now full.")
        } else {
            player.message("You operate the hopper. The grain slides down the chute.")
        }
    }

    @UseOn("grain", "hopper")
    suspend fun use(player: Player, target: GameObject) {
        if (player.quest("cooks_assistant") != "started") {
            player.anim("fill_hopper")
            player.inventory.remove("grain")
            player["hopper_bin"] = 1
            player.message("You put the grain in the hopper. You should now pull the lever nearby to operate the hopper.")
            return
        }
        if (player["cooks_assistant_talked_to_millie", 0] == 0) {
            player.dialogue { player<Talk>("Hmm. I should probably ask that lady downstairs how I can make extra fine flour.") }
            return
        }
        if (player.holdsItem("extra_fine_flour")) {
            player.message("It'd be best to take the extra fine flour you already have to the cook first.")
            return
        }
        if (player.bank.contains("extra_fine_flour")) {
            player.message("It'd be best to take the extra fine flour you already have in your bank to the cook first.")
            return
        }
        if (player["hopper_bin", 0] == 1) {
            player.message("There is already grain in the hopper.")
        } else {
            player.anim("fill_hopper")
            player.inventory.remove("grain")
            player["hopper_bin"] = 1
            player.message("You put the grain in the hopper. You should now pull the lever nearby to operate the hopper.")
        }
    }

    @Option("Take-flour", "flour_bin")
    fun takeFlour(player: Player, target: GameObject) {
        if (!player.holdsItem("empty_pot")) {
            player.message("You need an empty pot to hold the flour in.")
            return
        }
        if (player.quest("cooks_assistant") == "started" && player["cooks_assistant_talked_to_millie", 0] == 1) {
            player.inventory.remove("empty_pot")
            if (player.holdsItem("extra_fine_flour") || player.bank.contains("extra_fine_flour")) {
                player.inventory.add("pot_of_flour")
                player.message("You fill a pot with flour from the bin.")
            } else {
                player.inventory.add("extra_fine_flour")
                player.message("You fill a pot with the extra fine flour from the bin.")
            }
            player.dec("flour_bin")
        } else {
            player.inventory.replace("empty_pot", "pot_of_flour")
            player.dec("flour_bin")
            player.message("You fill a pot with flour from the bin.")
        }
    }

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("flour_bin")
    }

}

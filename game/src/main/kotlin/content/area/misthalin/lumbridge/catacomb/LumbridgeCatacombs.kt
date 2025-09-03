package content.area.misthalin.lumbridge.catacomb

import content.entity.combat.killer
import content.entity.death.npcDeath
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.statement
import content.entity.player.inv.item.destroy.destroyed
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Death
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Subscribe

class LumbridgeCatacombs {

    @Option("Take", "*_demon_statuette")
    suspend fun operate(player: Player, target: GameObject, def: ObjectDefinition) = player.dialogue {
        if (player[def.stringId, "take"] != "take") {
            player.message("You've already taken this statuette.")
            return@dialogue
        }
        statement("The air grows tense as you approach the statuette. You sense a hostile presence nearby...")

        choice {
            option("Take the statuette.") {
                if (player.inventory.add(def.stringId)) {
                    player[def.stringId] = "plinth"
                    player.message("You carefully take the ${def.stringId}")
                } else {
                    player.inventoryFull()
                }
            }
            option("Leave it alone.")
        }
    }

    @Option("Take", "diamond_demon_statuette")
    fun take(player: Player, target: GameObject) {
        if (player["diamond_demon_statuette", "take_shield"] != "take") {
            return
        }
        if (player.inventory.add("diamond_demon_statuette")) {
            player["diamond_demon_statuette"] = "touch"
        }
    }

    @Death("dragith_nurn")
    fun death(npc: NPC) {
        val killer = npc.killer
        if (killer is Player) {
            killer.message("With Dragith Nurn defeated, the diamond statuette is now within your grasp.")
            killer["diamond_demon_statuette"] = "take"
        }
    }

    @Subscribe("destroyed", "*_demon_statuette")
    fun destroy(player: Player, id: String) {
        player[id] = "take"
    }

}

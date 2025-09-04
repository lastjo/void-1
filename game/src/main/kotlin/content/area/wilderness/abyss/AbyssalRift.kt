package content.area.wilderness.abyss

import content.quest.questCompleted
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Teleport

class AbyssalRift {

    @Teleport("Exit-through", "*_rift")
    fun tele(player: Player, target: GameObject, obj: ObjectDefinition): Int {
        when {
            obj.stringId == "cosmic_rift" && !player.questCompleted("lost_city") -> {
                player.message("You need to have completed the Lost City Quest to use this rift.")
                return -1
            }
            obj.stringId == "law_rift" -> {
                // TODO proper message
                player.message("You cannot carry any weapons or armour through this rift.")
                return -1
            }
            obj.stringId == "death_rift" && !player.questCompleted("mournings_end_part_2") -> {
                player.message("A strange power blocks your exit.")
                return -1
            }
            obj.stringId == "blood_rift" && !player.questCompleted("legacy_of_seergaze") -> {
                player.message("You need to have completed the Legacy of Seergaze quest to use this rift.")
                return -1
            }
            obj.stringId == "soul_rift" -> {
                return -1
            }
        }
        return 0
    }

    @Option("Exit-through", "soul_rift")
    fun operate(player: Player, target: GameObject): Boolean {
        player.message("You have not yet unlocked this rift.")
        return true
    }
}

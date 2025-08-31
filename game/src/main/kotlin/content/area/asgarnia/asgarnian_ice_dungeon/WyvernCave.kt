package content.area.asgarnia.asgarnian_ice_dungeon

import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.statement
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Option

class WyvernCave {

    // Wyvern cave entrance (Ice Dungeon)
    @Option("Exit", "ice_dungeon_wyvern_cave_exit")
    suspend fun enter(player: Player, obj: GameObject) = player.dialogue {
        val slayerLevel = player.levels.get(Skill.Slayer)
        if (slayerLevel < 72) {
            statement("You need a Slayer level of 72 to enter this cave.")
            return@dialogue
        }
        statement("It's very cold in there... Are you sure you want to enter?")
        choice {
            option("Yes, I'll brave the cold.") {
                player.message("You squeeze through the icy gap...")
                player.tele(Tile(3056, 9555, 0)) // Location inside Ice Dungeon for Wyverns
            }
            option("No, it's too cold for me.") {
                player.message("You decide to stay where it's warmer.")
            }
        }
    }

    // Exit back to Ice Dungeon
    @Option("Enter", "ice_dungeon_wyvern_cave_enter")
    suspend fun exit(player: Player, obj: GameObject) = player.dialogue {
        statement("This passage seems to lead back into the main Ice Dungeon.")
        choice {
            option("Leave the cave.") {
                player.message("You crawl back through the gap.")
                player.tele(Tile(3056, 9562, 0)) // Just outside the entrance
            }
            option("Stay in the cave.") {
                player.message("You decide to stay here a while longer.")
            }
        }
    }

}

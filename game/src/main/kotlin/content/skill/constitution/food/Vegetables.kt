package content.skill.constitution.food

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.male
import world.gregs.voidps.type.sub.Consume

class Vegetables {

    @Consume("cabbage")
    fun cabbage(player: Player): Boolean {
        player.message("You don't really like it much.", ChatType.Filter)
        return false
    }

    @Consume("onion")
    fun onion(player: Player): Boolean {
        player.message("It hurts to see a grown ${if (player.male) "male" else "female"} cry.", ChatType.Filter)
        return false
    }
}

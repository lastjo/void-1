package content.skill.constitution.food

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.male
import world.gregs.voidps.type.sub.Consume

class Vegetables {

    @Consume("cabbage")
    fun cabbage(player: Player) {
        player.message("You don't really like it much.", ChatType.Filter)
    }

    @Consume("onion")
    fun onion(player: Player) {
        player.message("It hurts to see a grown ${if (player.male) "male" else "female"} cry.", ChatType.Filter)
    }
}

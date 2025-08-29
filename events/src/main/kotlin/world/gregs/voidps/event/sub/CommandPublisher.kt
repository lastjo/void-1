package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import world.gregs.voidps.type.PlayerRights

class CommandPublisher: Publisher(
    name = "CommandPublisher",
    parameters = listOf(
        "player" to PLAYER,
        "prefix" to STRING,
        "content" to STRING,
        "rights" to INT,
    ),
    overrideMethod = "command"
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val rights = method.annotationArgs["rights"] as Int
        val list = mutableListOf<Pair<String, Any>>()
        if (rights != PlayerRights.NONE) {
            list.add("rights" to rights)
        }
        if (ids.isEmpty()) {
            throw IllegalArgumentException("Command ids cannot be empty")
        }
        return ids.map { listOf("prefix" to it.substringBefore("[").substringBefore("(").trim()) }
    }
}

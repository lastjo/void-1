package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class SubscribePublisher: Publisher(
    name = "SubscribePublisher",
    parameters = listOf(
        "player" to PLAYER,
        "event" to STRING,
        "id" to STRING,
    ),
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val event = method.annotationArgs["event"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Pair<String, Any>>()
        if (event != "*") {
            list.add("event" to event)
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf("id" to it) }
    }
}

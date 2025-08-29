package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class OpenPublisher : Publisher(
    name = "OpenInterfacePublisher",
    parameters = listOf(
        "player" to PLAYER,
        "id" to STRING,
    ),
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("id" to it) }
    }
}

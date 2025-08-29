package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class ClosePublisher :
    Publisher(
        name = "CloseInterfacePublisher",
        parameters = listOf(
            "player" to PLAYER,
            "id" to STRING,
        ),
        overrideMethod = "interfaceClosed",
    ) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("id" to it) }
    }
}

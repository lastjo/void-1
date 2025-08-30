package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class ClosePublisher : Publisher(Publishers::interfaceClosed) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("id" to it) }
    }
}

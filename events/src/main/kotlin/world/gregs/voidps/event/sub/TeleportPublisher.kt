package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class TeleportPublisher : Publisher(Publishers::teleport) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val land = method.annotationArgs["land"] as Boolean
        val list = mutableListOf<Pair<String, Any>>()
        list.add("land" to land)
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf("def.stringId" to it) }
    }
}

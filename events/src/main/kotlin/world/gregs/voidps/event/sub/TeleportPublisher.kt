package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class TeleportPublisher : Publisher(Publishers::teleport, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val land = method.annotationArgs["land"] as Boolean
        val list = mutableListOf<Comparator>()
        list.add(Equals("land", land))
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("def.stringId", it)) }
    }
}

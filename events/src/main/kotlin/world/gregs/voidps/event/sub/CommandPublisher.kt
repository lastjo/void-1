package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import world.gregs.voidps.type.PlayerRights

class CommandPublisher : Publisher(Publishers::command) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val rights = method.annotationArgs["rights"] as Int
        val list = mutableListOf<Comparator>()
        if (rights != PlayerRights.NONE) {
            list.add(Equals("rights", rights))
        }
        if (ids.isEmpty()) {
            throw IllegalArgumentException("Command ids cannot be empty")
        }
        return ids.map { listOf(Equals("prefix", it.substringBefore("[").substringBefore("(").trim())) }
    }
}

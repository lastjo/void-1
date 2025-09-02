package world.gregs.voidps.event.map

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import world.gregs.voidps.type.PlayerRights

class CommandPublisherMapping : PublisherMapping(Publishers::command) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val rights = method.annotationArgs["rights"] as Int
        val list = mutableListOf<Condition>()
        if (rights != PlayerRights.NONE) {
            list.add(Equals("rights", rights))
        }
        if (ids.isEmpty()) {
            throw IllegalArgumentException("Command ids cannot be empty")
        }
        return ids.map { listOf(Equals("prefix", it.substringBefore("[").substringBefore("(").trim())) }
    }
}

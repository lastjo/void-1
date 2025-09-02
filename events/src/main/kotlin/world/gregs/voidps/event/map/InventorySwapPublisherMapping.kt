package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InventorySwapPublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val fromId = method.annotationArgs["fromId"] as String
        val fromComponent = method.annotationArgs["fromComponent"] as String
        val toId = method.annotationArgs["toId"] as String
        val toComponent = method.annotationArgs["toComponent"] as String
        val list = mutableListOf<Condition>()
        if (fromId != "*") {
            list.add(Equals("id", fromId))
        }
        if (toId != "*") {
            list.add(Equals("toId", toId))
        }
        if (fromComponent != "*") {
            list.add(Equals("component", fromComponent))
        }
        if (toComponent != "*") {
            list.add(Equals("toComponent", toComponent))
        }
        return listOf(list)
    }
}

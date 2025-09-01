package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InventorySwapPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val fromId = method.annotationArgs["fromId"] as String
        val fromComponent = method.annotationArgs["fromComponent"] as String
        val toId = method.annotationArgs["toId"] as String
        val toComponent = method.annotationArgs["toComponent"] as String
        val list = mutableListOf<Comparator>()
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

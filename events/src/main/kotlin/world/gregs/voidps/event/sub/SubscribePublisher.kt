package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class SubscribePublisher(function: KFunction<*>) : Publisher(function, notification = true, cancellable = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val event = method.annotationArgs["event"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Comparator>()
        if (event != "*") {
            list.add(Equals("event", event))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("id", it)) }
    }
}

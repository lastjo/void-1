package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class SubscribePublisher(function: KFunction<*>) : Publisher(function, notification = true, cancellable = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val event = method.annotationArgs["event"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Pair<String, Any>>()
        if (event != "*") {
            list.add("event" to event)
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf("id" to it) }
    }
}

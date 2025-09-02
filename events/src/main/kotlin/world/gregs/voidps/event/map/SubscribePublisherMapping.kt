package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class SubscribePublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true, cancellable = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val event = method.annotationArgs["event"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Condition>()
        if (event != "*") {
            list.add(Equals("event", event))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("id", it)) }
    }
}

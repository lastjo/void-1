package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class TimerPublisher(function: KFunction<*>) : Publisher(function) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("timer" to it) }
    }
}

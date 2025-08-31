package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ConsumePublisher(function: KFunction<*>) : Publisher(function, notification = true, cancellable = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        return ids.map { listOf("item.id" to it) }
    }
}

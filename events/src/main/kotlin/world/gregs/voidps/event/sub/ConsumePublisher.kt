package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ConsumePublisher(function: KFunction<*>) : Publisher(function, notification = true, cancellable = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return listOf(emptyList())
        }
        return ids.map { listOf(Equals("item.id", it)) }
    }
}

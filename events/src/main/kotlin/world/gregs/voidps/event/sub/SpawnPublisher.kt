package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class SpawnPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return listOf()
        }
        return ids.map { listOf(Equals("${parameters.first().first}.id", it)) }
    }
}

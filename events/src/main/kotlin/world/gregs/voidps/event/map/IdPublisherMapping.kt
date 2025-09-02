package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

open class IdPublisherMapping(
    function: KFunction<*>,
    private val field: String = "id",
    notification: Boolean = false
) : PublisherMapping(function, notification = notification) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return listOf(emptyList())
        }
        return ids.map { listOf(Equals(field, it)) }
    }
}

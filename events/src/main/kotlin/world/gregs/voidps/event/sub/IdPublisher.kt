package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

open class IdPublisher(
    function: KFunction<*>,
    private val field: String = "id",
    notification: Boolean = false
) : Publisher(function, notification = notification) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf(field to it) }
    }
}

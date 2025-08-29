package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class TimerStartPublisher(field: String, type: ClassName): Publisher(
    name = "StartTimerPublisher",
    parameters = listOf(
        field to type,
        "timer" to STRING,
        "restart" to BOOLEAN,
    ),
    returnsDefault = -1
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("timer" to it) }
    }
}

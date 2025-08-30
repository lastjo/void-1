package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class SubscribePublisher(field: String, type: ClassName) : Publisher(
        name = "${type.simpleName}SubscribePublisher",
        parameters = listOf(
            field to type,
            "event" to STRING,
            "id" to STRING,
        ),
        overrideMethod = "publish${type.simpleName}",
        notification = true,
    ) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
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

class EmptySubscribePublisher :
    Publisher(
        name = "SubscribePublisher",
        parameters = listOf(
            "event" to STRING,
            "id" to STRING,
        ),
        overrideMethod = "publish",
        notification = true,
    ) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
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

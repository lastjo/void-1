package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.script.PLAYER
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Subscribe(
    val event: String = "*",
    vararg val ids: String,
)

/**
 * Generic event handling
 */
class SubscribePublisher: Publisher(
    name = "SubscribePublisher",
    parameters = listOf(
        "player" to PLAYER,
        "event" to STRING,
        "id" to STRING,
    ),
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
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

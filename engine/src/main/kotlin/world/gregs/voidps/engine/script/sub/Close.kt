package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.script.PLAYER
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Close(
    vararg val ids: String,
)

class ClosePublisher: Publisher(
    name = "CloseInterfacePublisher",
    parameters = listOf(
        "player" to PLAYER,
        "id" to STRING,
    ),
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("id" to it) }
    }
}

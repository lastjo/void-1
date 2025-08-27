package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStop(
    vararg val ids: String,
)

class TimerStopPublisher(val field: String, type: ClassName): Publisher(
    name = "StartTimerPublisher",
    parameters = listOf(
        field to type,
        "timer" to STRING,
        "logout" to BOOLEAN,
    ),
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return emptyList()
        }
        return ids.map { listOf("timer" to it) }
    }
}

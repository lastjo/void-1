package world.gregs.voidps.engine.script

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Option(
    val option: String = "*",
    vararg val ids: String
)

class OptionPublisher(target: ClassName): Publisher(
    name = "Player${target.simpleName}Publisher",
    parameters = listOf(
        "player" to PLAYER,
        "target" to target,
        "option" to STRING,
    ),
    suspendable = true
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val option = method.annotationArgs["option"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        if (option == "*") {
            return ids.map { listOf("target.id" to it) }
        }
        if (ids.isEmpty()) {
            return listOf(listOf("option" to option))
        }
        return ids.map { listOf("option" to option, "target.id" to it) }
    }
}

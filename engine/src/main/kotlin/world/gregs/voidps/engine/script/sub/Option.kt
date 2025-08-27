package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.script.PLAYER
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Option(
    val option: String = "*",
    vararg val ids: String,
    val approach: Boolean = false,
)

class OptionPublisher(target: ClassName): Publisher(
    name = "Player${target.simpleName}Publisher",
    parameters = listOf(
        "player" to PLAYER,
        "target" to target,
        "option" to STRING,
        "approach" to BOOLEAN,
    ),
    suspendable = true
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val option = method.annotationArgs["option"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val approach = method.annotationArgs["approach"] as Boolean
        val list = mutableListOf<Pair<String, Any>>()
        if (option != "*") {
            list.add("option" to option)
        }
        if (approach) {
            list.add("approach" to true)
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf("target.id" to it) }
    }
}

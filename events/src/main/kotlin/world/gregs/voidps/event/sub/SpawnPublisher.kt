package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class SpawnPublisher(val field: String, target: ClassName): Publisher(
    name = "Spawn${target.simpleName}Publisher",
    parameters = listOf(
        field to target
    ),
    notification = true
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return listOf()
        }
        return ids.map { listOf("${field}.id" to it) }
    }
}

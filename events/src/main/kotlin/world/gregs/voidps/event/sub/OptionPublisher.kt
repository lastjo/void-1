package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class OptionPublisher(field: String, source: ClassName, target: ClassName): Publisher(
    name = "${source.simpleName}${target.simpleName}Publisher",
    parameters = listOf(
        field to source,
        "target" to target,
        "option" to STRING,
        "approach" to BOOLEAN,
    ),
    suspendable = true
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
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

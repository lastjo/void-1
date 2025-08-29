package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.ClassName
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class DespawnPublisher(private val field: String, target: ClassName) :
    Publisher(
        name = "Despawn${target.simpleName}Publisher",
        parameters = listOf(
            field to target,
        ),
        notification = true,
        overrideMethod = "despawn",
    ) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        if (ids.isEmpty()) {
            return listOf()
        }
        return ids.map { listOf("$field.id" to it) }
    }
}

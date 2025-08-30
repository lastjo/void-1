package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class OptionPublisher(function: KFunction<*>, has: KFunction<*>) : Publisher(function, has) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val option = method.annotationArgs["option"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val approach = method.annotationArgs["approach"] as Boolean
        val list = mutableListOf<Pair<String, Any>>()
        if (option != "*") {
            list.add("option" to option)
        }
        list.add("approach" to approach)
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return if (name.endsWith("GameObjectOptionPublisher") || name.endsWith("NPCOptionPublisher")) {
            ids.map { list + listOf("def.stringId" to it) }
        } else {
            ids.map { list + listOf("target.id" to it) }
        }
    }
}

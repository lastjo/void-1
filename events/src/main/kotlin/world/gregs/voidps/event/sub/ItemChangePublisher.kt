package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ItemChangePublisher(function: KFunction<*>) : Publisher(function) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val slots = method.annotationArgs["slots"] as List<Int>
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Pair<String, Any>>()
        if (inventory != "*") {
            list.add(Pair("inventory", inventory))
        }
        if (ids.isNotEmpty() && slots.isNotEmpty()) {
            val lists = mutableListOf<List<Pair<String, Any>>>()
            for (id in ids) {
                for (slot in slots) {
                    lists.add(list + listOf("item.id" to id, "index" to slot))
                }
            }
            return lists
        } else if (ids.isNotEmpty()) {
            return ids.map { list + listOf("item.id" to it) }
        } else if (slots.isNotEmpty()) {
            return slots.map { list + listOf("index" to it) }
        } else {
            return listOf(list)
        }
    }
}

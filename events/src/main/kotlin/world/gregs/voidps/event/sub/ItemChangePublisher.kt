package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ItemChangePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val slots = method.annotationArgs["slots"] as List<Int>
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Comparator>()
        if (inventory != "*") {
            list.add(Equals("inventory", inventory))
        }
        if (ids.isNotEmpty() && slots.isNotEmpty()) {
            val lists = mutableListOf<List<Comparator>>()
            for (id in ids) {
                for (slot in slots) {
                    lists.add(list + listOf(Equals("item.id", id), Equals("itemSlot", slot)))
                }
            }
            return lists
        } else if (ids.isNotEmpty()) {
            return ids.map { list + listOf(Equals("item.id", it)) }
        } else if (slots.isNotEmpty()) {
            return slots.map { list + listOf(Equals("itemSlot", it)) }
        } else {
            return listOf(list)
        }
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InventoryChangePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val slot = method.annotationArgs["slot"] as Int
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Pair<String, Any>>()
        if (inventory != "*") {
            list.add("inventory" to inventory)
        }
        if (slot != -1) {
            list.add("slot" to slot)
        }
        return listOf(list)
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InventoryChangePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val slot = method.annotationArgs["slot"] as Int
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Condition>()
        if (inventory != "*") {
            list.add(Equals("inventory", inventory))
        }
        if (slot != -1) {
            list.add(Equals("itemSlot", slot))
        }
        return listOf(list)
    }
}

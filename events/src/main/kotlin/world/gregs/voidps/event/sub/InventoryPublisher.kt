package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class InventoryPublisher : Publisher(Publishers::inventoryOption) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val option = method.annotationArgs["option"] as String
        val item = method.annotationArgs["item"] as String
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Comparator>()
        if (option != "*") {
            list.add(Equals("option", option))
        }
        if (item != "*") {
            list.add(Equals("item.id", item))
        }
        if (inventory != "*") {
            list.add(Equals("inventory", inventory))
        }
        return listOf(list)
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class InventoryPublisher : Publisher(Publishers::inventoryOption) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val option = method.annotationArgs["option"] as String
        val item = method.annotationArgs["item"] as String
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Pair<String, String>>()
        if (option != "*") {
            list.add("option" to option)
        }
        if (item != "*") {
            list.add("item.id" to item)
        }
        if (inventory != "*") {
            list.add("inventory" to inventory)
        }
        return listOf(list)
    }
}

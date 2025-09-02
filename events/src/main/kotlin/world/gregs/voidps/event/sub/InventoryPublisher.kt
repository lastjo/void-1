package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class InventoryPublisher : Publisher(Publishers::inventoryOption) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val option = method.annotationArgs["option"] as String
        val item = method.annotationArgs["item"] as String
        val inventory = method.annotationArgs["inventory"] as String
        val list = mutableListOf<Condition>()
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

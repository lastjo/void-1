package world.gregs.voidps.event.sub

import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class InterfacePublisher : Publisher(Publishers::interfaceOption) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val option = method.annotationArgs["option"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String

        val list = mutableListOf<Pair<String, String>>()
        if (option != "*") {
            list.add("option" to option)
        }
        if (id != "*") {
            list.add("id" to id)
        }
        if (component != "*") {
            list.add("component" to component)
        }
        return listOf(list)
    }
}

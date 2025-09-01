package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InterfacePublisher(function: KFunction<*>) : Publisher(function) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val option = method.annotationArgs["option"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val list = mutableListOf<Comparator>()
        if (option != "*") {
            list.add(Equals("option", option))
        }
        if (id != "*") {
            list.add(Equals("id", id))
        }
        if (component != "*") {
            list.add(Equals("component", component))
        }
        return listOf(list)
    }
}

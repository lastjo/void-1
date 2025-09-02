package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InterfacePublisher(function: KFunction<*>) : Publisher(function) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val option = method.annotationArgs["option"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val list = mutableListOf<Condition>()
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

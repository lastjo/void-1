package world.gregs.voidps.event.map

import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class InterfacePublisherMapping(function: KFunction<*>) : PublisherMapping(function) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val option = method.annotationArgs["option"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val list = mutableListOf<Condition>()
        if (option != "*") {
            list.add(Equals("option", option))
        }
        if (id != "*") {
            if (name == "ContinueDialogueItemPublisher") {
                list.add(Equals("item.id", id))
            } else {
                list.add(Equals("id", id))
            }
        }
        if (component != "*") {
            list.add(Equals("component", component))
        }
        return listOf(list)
    }
}

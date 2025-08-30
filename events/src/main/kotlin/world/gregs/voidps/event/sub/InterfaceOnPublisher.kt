package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InterfaceOnPublisher(function: KFunction<*>, has: KFunction<*>) : Publisher(function, has) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val item = method.annotationArgs["item"] as String
        val on = method.annotationArgs["on"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val approach = method.annotationArgs["approach"] as Boolean

        val list = mutableListOf<Pair<String, Any>>()
        if (item != "*") {
            list.add("item.id" to item)
        }
        if (on != "*") {
            if (name == "InterfaceOnGameObjectPublisher" || name == "InterfaceOnNPCPublisher") {
                list.add("def.stringId" to on)
            } else {
                list.add("target.id" to on)
            }
        }
        if (id != "*") {
            list.add("id" to id)
        }
        if (component != "*") {
            list.add("component" to component)
        }
        list.add("approach" to approach)
        return listOf(list)
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class AreaPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val area = method.annotationArgs["area"] as String
        val tag = method.annotationArgs["tag"] as String
        val list = mutableListOf<Pair<String, Any>>()
        if (area != "*") {
            list.add("name" to area)
        }
        if (tag != "*") {
            list.add("tag" to tag)
        }
        return listOf(list)
    }
}

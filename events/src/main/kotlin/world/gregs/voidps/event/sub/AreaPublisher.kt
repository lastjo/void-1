package world.gregs.voidps.event.sub

import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class AreaPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val area = method.annotationArgs["area"] as String
        val tag = method.annotationArgs["tag"] as String
        val list = mutableListOf<Comparator>()
        if (area != "*") {
            list.add(Equals("name", area))
        }
        if (tag != "*") {
            list.add(Contains("tags", tag))
        }
        return listOf(list)
    }
}

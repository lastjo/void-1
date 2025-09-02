package world.gregs.voidps.event.map

import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class AreaPublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val area = method.annotationArgs["area"] as String
        val tag = method.annotationArgs["tag"] as String
        val list = mutableListOf<Condition>()
        if (area != "*") {
            list.add(Equals("name", area))
        }
        if (tag != "*") {
            list.add(Contains("tags", tag))
        }
        return listOf(list)
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class TeleportPublisher(function: KFunction<*>, notification: Boolean) : Publisher(function, returnsDefault = if (notification) false else 0) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val type = method.annotationArgs["type"] as String
        val list = mutableListOf<Comparator>()
        if (type != "*") {
            if (name.endsWith("GameObjectPublisher")) {
                list.add(Equals("option", type))
            } else {
                list.add(Equals("type", type))
            }
        }
        if (ids.isEmpty()) {
            return listOf()
        }
        return ids.map { list + listOf(Equals("def.stringId", it)) }
    }
}

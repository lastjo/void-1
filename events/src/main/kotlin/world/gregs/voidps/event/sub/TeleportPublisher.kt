package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class TeleportPublisher(function: KFunction<*>, notification: Boolean) : Publisher(function, returnsDefault = if (notification) false else 0) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val type = method.annotationArgs["type"] as String
        val list = mutableListOf<Condition>()
        if (type != "*") {
            if (name.endsWith("GameObjectPublisher")) {
                list.add(Equals("option", type))
            } else {
                list.add(Equals("type", type))
            }
        }
        if (ids.isEmpty()) {
            return listOf(emptyList())
        }
        return ids.map { list + listOf(Equals("def.stringId", it)) }
    }
}

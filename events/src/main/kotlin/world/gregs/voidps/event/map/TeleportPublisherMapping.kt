package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class TeleportPublisherMapping(function: KFunction<*>, notification: Boolean) : PublisherMapping(function, returnsDefault = if (notification) false else 0) {
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
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("def.stringId", it)) }
    }
}

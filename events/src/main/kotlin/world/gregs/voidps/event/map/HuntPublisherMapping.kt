package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class HuntPublisherMapping(function: KFunction<*>) : PublisherMapping(function) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val mode = method.annotationArgs["mode"] as String
        val npc = method.annotationArgs["npc"] as String
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Condition>()
        if (mode != "*") {
            list.add(Equals("mode", mode))
        }
        if (npc != "*") {
            list.add(Equals("target.id", npc))
        }
        if (id != "*") {
            list.add(Equals("npc.id", id))
        }
        return listOf(list)
    }
}

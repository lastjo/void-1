package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class HuntPublisher(function: KFunction<*>) : Publisher(function) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val mode = method.annotationArgs["mode"] as String
        val npc = method.annotationArgs["npc"] as String
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Comparator>()
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

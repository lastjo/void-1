package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class CombatPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val weapon = method.annotationArgs["weapon"] as String
        val type = method.annotationArgs["type"] as String
        val spell = method.annotationArgs["spell"] as String
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Comparator>()
        if (weapon != "*") {
            list.add(Equals("weapon.id", weapon))
        }
        if (type != "*") {
            list.add(Equals("type", type))
        }
        if (spell != "*") {
            list.add(Equals("spell", spell))
        }
        if (id != "*") {
            list.add(Equals("def.id", id))
        }
        return listOf(list)
    }
}

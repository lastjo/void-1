package world.gregs.voidps.event.map

import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class CombatPublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val weapon = method.annotationArgs["weapon"] as String
        val type = method.annotationArgs["type"] as String
        val spell = method.annotationArgs["spell"] as String
        val id = method.annotationArgs["id"] as String
        val stage = method.annotationArgs["stage"] as Int
        val list = mutableListOf<Condition>()
        list.add(Equals("stage", stage))
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
            list.add(Equals("source.id", id))
        }
        return listOf(list)
    }
}

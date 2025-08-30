package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class CombatPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val weapon = method.annotationArgs["weapon"] as String
        val type = method.annotationArgs["type"] as String
        val spell = method.annotationArgs["spell"] as String
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Pair<String, Any?>>()
        if (weapon != "*") {
            list.add("weapon.id" to weapon)
        }
        if (type != "*") {
            list.add("type" to type)
        }
        if (spell != "*") {
            list.add("spell" to spell)
        }
        if (id != "*") {
            list.add("def.id" to id)
        }
        return listOf(list)
    }
}

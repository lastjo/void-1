package world.gregs.voidps.event.map

import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class SpecialAttackPublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true, cancellable = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Condition>()
        if (name != "SpecialAttackPreparePublisher") {
            val damage = method.annotationArgs["damage"] as Boolean
            if (damage) {
                list.add(GreaterThan("damage", -2))
            } else {
                list.add(Equals("damage", -2))
            }
        }
        if (id != "*") {
            list.add(Equals("id", id))
        }
        return listOf(list)
    }
}

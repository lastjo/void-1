package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class SpecialAttackPublisher(function: KFunction<*>) : Publisher(function, notification = true, cancellable = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val id = method.annotationArgs["id"] as String
        val prepare = method.annotationArgs["prepare"] as Boolean
        val list = mutableListOf<Condition>()
        list.add(Equals("prepare", prepare))
        if (id != "*") {
            list.add(Equals("id", id))
        }
        return listOf(list)
    }
}

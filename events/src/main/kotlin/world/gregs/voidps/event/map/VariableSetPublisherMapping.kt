package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class VariableSetPublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val ids = method.annotationArgs["keys"] as List<String>
        val from = method.annotationArgs["from"] as String
        val to = method.annotationArgs["to"] as String
        val fromInt = method.annotationArgs["fromInt"] as Int
        val toInt = method.annotationArgs["toInt"] as Int
        val fromNull = method.annotationArgs["fromNull"] as Boolean
        val toNull = method.annotationArgs["toNull"] as Boolean
        val fromBool = method.annotationArgs["fromBool"] as String
        val toBool = method.annotationArgs["toBool"] as String
        val list = mutableListOf<Condition>()
        if (fromInt != -1) {
            list.add(Equals("from", fromInt))
        }
        if (toInt != -1) {
            list.add(Equals("to", toInt))
        }
        if (from != "*") {
            list.add(Equals("from", from))
        }
        if (to != "*") {
            list.add(Equals("to", to))
        }
        if (fromNull) {
            list.add(Equals("from", null))
        }
        if (toNull) {
            list.add(Equals("to", null))
        }
        if (fromBool != "*") {
            list.add(Equals("from", from.toBoolean(), explicit = true))
        }
        if (toBool != "*") {
            list.add(Equals("to", to.toBoolean(), explicit = true))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("id", it)) }
    }
}

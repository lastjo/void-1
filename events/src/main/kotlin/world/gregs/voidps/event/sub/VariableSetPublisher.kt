package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class VariableSetPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val ids = method.annotationArgs["keys"] as List<String>
        val from = method.annotationArgs["from"] as String
        val to = method.annotationArgs["to"] as String
        val fromInt = method.annotationArgs["fromInt"] as Int
        val toInt = method.annotationArgs["toInt"] as Int
        val fromNull = method.annotationArgs["fromNull"] as Boolean
        val toNull = method.annotationArgs["toNull"] as Boolean
        val fromBool = method.annotationArgs["fromBool"] as String
        val toBool = method.annotationArgs["toBool"] as String
        val list = mutableListOf<Comparator>()
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
            list.add(Equals("from", from.toBoolean(), simplify = false))
        }
        if (toBool != "*") {
            list.add(Equals("to", to.toBoolean(), simplify = false))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("id", it)) }
    }
}

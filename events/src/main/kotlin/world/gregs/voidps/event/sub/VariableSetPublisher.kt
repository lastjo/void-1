package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class VariableSetPublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any?>>> {
        val ids = method.annotationArgs["keys"] as List<String>
        val from = method.annotationArgs["from"] as String
        val to = method.annotationArgs["to"] as String
        val fromInt = method.annotationArgs["fromInt"] as Int
        val toInt = method.annotationArgs["toInt"] as Int
        val fromNull = method.annotationArgs["fromNull"] as Boolean
        val toNull = method.annotationArgs["toNull"] as Boolean
        val list = mutableListOf<Pair<String, Any?>>()
        if (fromInt != -1) {
            list.add("from" to fromInt)
        }
        if (toInt != -1) {
            list.add("to" to toInt)
        }
        if (from != "*") {
            list.add("from" to from)
        }
        if (to != "*") {
            list.add("to" to to)
        }
        if (fromNull) {
            list.add("from" to null)
        }
        if (toNull) {
            list.add("to" to null)
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf("id" to it) }
    }
}

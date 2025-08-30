package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class LevelChangePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val skills = method.annotationArgs["skills"] as List<String>
        val from = method.annotationArgs["from"] as Int
        val to = method.annotationArgs["to"] as Int
        val max = method.annotationArgs["max"] as Boolean
        val list = mutableListOf<Pair<String, Any>>()
        list.add("max" to max)
        if (from != -1) {
            list.add("from" to from)
        }
        if (to != -1) {
            list.add("to" to to)
        }
        if (skills.isEmpty()) {
            return listOf(list)
        }
        return skills.map { list + listOf("skill.name" to it) }
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class LevelChangePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val skills = method.annotationArgs["skills"] as List<String>
        val from = method.annotationArgs["from"] as Int
        val to = method.annotationArgs["to"] as Int
        val max = method.annotationArgs["max"] as Boolean
        val id = method.annotationArgs["id"] as String
        val list = mutableListOf<Comparator>()
        list.add(Equals("max", max))
        if (id != "*") {
            list.add(Equals("npc.id", id))
        }
        if (from != -1) {
            list.add(Equals("from", from))
        }
        if (to != -1) {
            list.add(Equals("to", to))
        }
        if (skills.isEmpty()) {
            return listOf(list)
        }
        return skills.map { list + listOf(Equals("skill.name", it)) }
    }
}

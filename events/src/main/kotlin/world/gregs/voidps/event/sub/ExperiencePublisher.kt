package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ExperiencePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val skills = method.annotationArgs["skills"] as List<String>
        if (skills.isEmpty()) {
            return listOf(emptyList())
        }
        return skills.map { listOf(Equals("skill.name", it)) }
    }
}

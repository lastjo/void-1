package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class ExperiencePublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val skills = method.annotationArgs["skills"] as List<String>
        if (skills.isEmpty()) {
            return listOf(emptyList())
        }
        return skills.map { listOf(Equals("skill.name", it)) }
    }
}

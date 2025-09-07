package world.gregs.voidps.event.map

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.PublisherMapping
import world.gregs.voidps.event.Subscriber
import world.gregs.voidps.type.Tile
import kotlin.reflect.KFunction

class MovePublisherMapping(function: KFunction<*>) : PublisherMapping(function, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val from = method.annotationArgs["from"] as List<Int>
        val to = method.annotationArgs["to"] as List<Int>
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Condition>()
        if (from.isNotEmpty()) {
            val tile = Tile(from[0], from[1], from.getOrNull(2) ?: 0)
            list.add(Equals("from.id", tile.id))
        }
        if (to.isNotEmpty()) {
            val tile = Tile(to[0], to[1], to.getOrNull(2) ?: 0)
            list.add(Equals("to.id", tile.id))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("${method.parameters.firstOrNull()?.first}.id", it)) }
    }
}

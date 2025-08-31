package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import world.gregs.voidps.type.Tile
import kotlin.reflect.KFunction

class MovePublisher(function: KFunction<*>) : Publisher(function, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val from = method.annotationArgs["from"] as List<Int>
        val to = method.annotationArgs["to"] as List<Int>
        val ids = method.annotationArgs["ids"] as List<String>
        val list = mutableListOf<Comparator>()
        if (from.isNotEmpty()) {
            val tile = Tile(from[0], from[1], from.getOrNull(2) ?: 0)
            list.add(Equals("from", tile.id))
        }
        if (to.isNotEmpty()) {
            val tile = Tile(to[0], to[1], to.getOrNull(2) ?: 0)
            list.add(Equals("to", tile.id))
        }
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return ids.map { list + listOf(Equals("target.id", it)) }
    }
}

package world.gregs.voidps.event.sub

import world.gregs.voidps.event.Condition
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class OptionPublisher(function: KFunction<*>, has: KFunction<*>) : Publisher(function, has) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val option = method.annotationArgs["option"] as String
        val ids = method.annotationArgs["ids"] as List<String>
        val approach = method.annotationArgs["approach"] as Boolean
        val list = mutableListOf<Condition>()
        if (option != "*") {
            list.add(Equals("option", option))
        }
        list.add(Equals("approach", approach))
        if (ids.isEmpty()) {
            return listOf(list)
        }
        return if (name.endsWith("GameObjectOptionPublisher") || name.endsWith("NPCOptionPublisher")) {
            ids.map { list + listOf(Equals("def.stringId", it)) }
        } else {
            ids.map { list + listOf(Equals("target.id", it)) }
        }
    }
}

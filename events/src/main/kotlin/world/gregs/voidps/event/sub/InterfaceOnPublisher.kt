package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.TypeName
import world.gregs.voidps.event.Comparator
import world.gregs.voidps.event.Equals
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber
import kotlin.reflect.KFunction

class InterfaceOnPublisher(function: KFunction<*>, has: KFunction<*>) : Publisher(function, has, notification = true) {
    override fun comparisons(method: Subscriber): List<List<Comparator>> {
        val item = method.annotationArgs["item"] as String
        val on = method.annotationArgs["on"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val approach = method.annotationArgs["approach"] as Boolean

        val list = mutableListOf<Comparator>()
        if (item != "*") {
            list.add(Equals(if (name == "InterfaceOnItemPublisher") "fromItem.id" else "item.id", item))
        }
        if (on != "*") {
            when (name) {
                "InterfaceOnGameObjectPublisher", "InterfaceOnNPCPublisher" -> list.add(Equals("def.stringId", on))
                "InterfaceOnItemPublisher" -> list.add(Equals("toItem.id", on))
                else -> list.add(Equals("target.id", on))
            }
        }
        if (id != "*") {
            list.add(Equals("id", id))
        }
        if (component != "*") {
            list.add(Equals("component", component))
        }
        list.add(Equals("approach", approach))
        return listOf(list)
    }


    override fun match(method: Subscriber, count: MutableMap<TypeName, Int>): List<List<String>> {
        if (this.name == "InterfaceOnItemPublisher" && method.annotationArgs["bidirectional"] as Boolean) {
            return super.match(method, count) + listOf(method.parameters.map { (name, type) ->
                if (count.getOrDefault(type, 0) > 1) {
                    val override = when (name) {
                        "fromItem" -> "toItem"
                        "toItem" -> "fromItem"
                        "fromSlot" -> "toSlot"
                        "toSlot" -> "fromSlot"
                        else -> name
                    }
                    // match by name
                    parameters.firstOrNull { it.first == override }
                } else {
                    // match by type
                    parameters.firstOrNull { it.second == type }
                }?.first ?: error("Expected parameter [${parameters.filter { it.second == type }.joinToString(", ") { it.first }}] for ${method.methodName}($name: ${type.toString().substringAfter(".")}) in ${method.className}.")
            })
        }
        return super.match(method, count)
    }

}

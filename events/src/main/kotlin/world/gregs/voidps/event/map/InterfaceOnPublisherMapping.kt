package world.gregs.voidps.event.map

import com.squareup.kotlinpoet.TypeName
import world.gregs.voidps.event.*
import kotlin.reflect.KFunction

class InterfaceOnPublisherMapping(function: KFunction<*>, has: KFunction<*>? = null) : PublisherMapping(function, has, notification = true) {
    override fun conditions(method: Subscriber): List<List<Condition>> {
        val item = method.annotationArgs["item"] as String
        val on = method.annotationArgs["on"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val approach = method.annotationArgs["approach"] as Boolean
        val list = mutableListOf<Condition>()
        list.add(Equals("approach", approach))
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
        return listOf(list)
    }

    override fun methods(subscriber: Subscriber): List<Method> {
        val conditions = conditions(subscriber).first()
        val methods = mutableListOf<Method>()
        val count = mutableMapOf<TypeName, Int>()
        for ((_, type) in parameters) {
            count[type] = count.getOrDefault(type, 0) + 1
        }
        if (this.name == "InterfaceOnItemPublisher" && subscriber.annotationArgs["bidirectional"] as Boolean) {
            val args = matchNames(subscriber.parameters.map {
                when (it.first) {
                    "fromItem" -> "toItem"
                    "toItem" -> "fromItem"
                    "fromSlot" -> "toSlot"
                    "toSlot" -> "fromSlot"
                    else -> it.first
                } to it.second
            }, count, subscriber)
            val flipped = conditions.map { Equals(if (it.key == "toItem.id") "fromItem.id" else if (it.key == "fromItem.id") "toItem.id" else it.key, it.value) }
            methods.add(Method(flipped, suspendable, subscriber.className, subscriber.methodName, args, subscriber.returnType))
        }
        val args = matchNames(subscriber.parameters, count, subscriber)
        methods.add(Method(conditions, suspendable, subscriber.className, subscriber.methodName, args, subscriber.returnType))
        return methods
    }

}

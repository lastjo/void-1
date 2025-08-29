package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.ITEM
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber

class InterfaceOnPublisher(target: ClassName): Publisher(
    name = "InterfaceOn${target.simpleName}Publisher",
    parameters = listOf(
        "player" to PLAYER,
        "target" to target,
        "id" to STRING,
        "component" to STRING,
        "item" to ITEM,
        "itemSlot" to INT,
        "inventory" to STRING,
    ),
    suspendable = true,
    overrideMethod = "interfaceOn${target.simpleName}",
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val item = method.annotationArgs["item"] as String
        val on = method.annotationArgs["on"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String
        val approach = method.annotationArgs["approach"] as Boolean

        val list = mutableListOf<Pair<String, Any>>()
        if (item != "*") {
            list.add("item.id" to item)
        }
        if (on != "*") {
            list.add("target.id" to on)
        }
        if (id != "*") {
            list.add("id" to id)
        }
        if (component != "*") {
            list.add("component" to component)
        }
        if (approach) {
            list.add("approach" to true)
        }
        return listOf(list)
    }
}
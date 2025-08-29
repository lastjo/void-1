package world.gregs.voidps.event.sub

import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.event.ITEM
import world.gregs.voidps.event.PLAYER
import world.gregs.voidps.event.Publisher
import world.gregs.voidps.event.Subscriber


class InterfacePublisher : Publisher(
    name = "PlayerInterfacePublisher",
    parameters = listOf(
        "player" to PLAYER,
        "id" to STRING,
        "component" to STRING,
        "optionIndex" to INT,
        "option" to STRING,
        "item" to ITEM,
        "itemSlot" to INT,
        "inventory" to STRING,
    ),
    suspendable = true
) {
    override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
        val option = method.annotationArgs["option"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String

        val list = mutableListOf<Pair<String, String>>()
        if (option != "*") {
            list.add("option" to option)
        }
        if (id != "*") {
            list.add("id" to id)
        }
        if (component != "*") {
            list.add("component" to component)
        }
        return listOf(list)
    }
}
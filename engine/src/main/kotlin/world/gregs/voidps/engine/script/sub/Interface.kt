package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.script.ITEM
import world.gregs.voidps.engine.script.PLAYER
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Interface(
    val option: String = "*",
    val component: String = "*",
    val id: String = "*",
)

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
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
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
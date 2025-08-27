package world.gregs.voidps.engine.script

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING

@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UseOn(
    val item: String = "*",
    val on: String = "*",
    val id: String = "*",
    val component: String = "*",
)

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
    suspendable = true
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val item = method.annotationArgs["item"] as String
        val on = method.annotationArgs["on"] as String
        val id = method.annotationArgs["id"] as String
        val component = method.annotationArgs["component"] as String

        val list = mutableListOf<Pair<String, String>>()
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
        return listOf(list)
    }
}
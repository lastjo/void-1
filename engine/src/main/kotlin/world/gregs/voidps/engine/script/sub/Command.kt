package world.gregs.voidps.engine.script.sub

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import world.gregs.voidps.engine.entity.character.player.PlayerRights
import world.gregs.voidps.engine.script.PLAYER
import world.gregs.voidps.engine.script.Publisher
import world.gregs.voidps.engine.script.Subscriber

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Command(
    vararg val ids: String,
    val description: String = "",
    val rights: Int = PlayerRights.NONE
)

class CommandPublisher: Publisher(
    name = "CommandPublisher",
    parameters = listOf(
        "player" to PLAYER,
        "prefix" to STRING,
        "rights" to INT,
        "command" to STRING,
    ),
) {
    override fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>> {
        val ids = method.annotationArgs["ids"] as List<String>
        val rights = method.annotationArgs["rights"] as Int
        val list = mutableListOf<Pair<String, Any>>()
        if (rights != PlayerRights.NONE) {
            list.add("rights" to rights)
        }
        if (ids.isEmpty()) {
            throw IllegalArgumentException("Command ids cannot be empty")
        }
        return ids.map { listOf("prefix" to it.substringBefore("[").substringBefore("(").trim()) }
    }
}

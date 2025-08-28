package world.gregs.voidps.engine.script

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

/**
 * Mapping [Subscriber] and [Annotation] into an Event's Publisher class
 */
abstract class Publisher(
    val name: String,
    val suspendable: Boolean = false,
    val parameters: List<Pair<String, ClassName>>,
    var returnsDefault: Any = false,
    var notification: Boolean = false
) {
    init {
        if (notification) {
            assert(returnsDefault is Boolean) { "Notification methods must return cancellation boolean." }
        }
    }
    abstract fun comparisons(builder: CodeBlock.Builder, method: Subscriber, methodName: String): List<List<Pair<String, Any>>>
}

internal val PLAYER = ClassName("world.gregs.voidps.engine.entity.character.player", "Player")
internal val CHARACTER = ClassName("world.gregs.voidps.engine.entity.character.", "Character")
internal val NPC = ClassName("world.gregs.voidps.engine.entity.character.npc", "NPC")
internal val OBJECT = ClassName("world.gregs.voidps.engine.entity.obj", "GameObject")
internal val FLOOR_ITEM = ClassName("world.gregs.voidps.engine.entity.item.floor", "FloorItem")
internal val ITEM = ClassName("world.gregs.voidps.engine.entity.item", "Item")
internal val WORLD = ClassName("world.gregs.voidps.engine.entity", "World")


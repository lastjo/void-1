package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName

/**
 * A method that wishes to subscribe to published events
 */
data class Method(
    val conditions: List<Condition>,
    val suspendable: Boolean,
    val className: ClassName,
    val methodName: String,
    val arguments: List<String> = emptyList(),
    val methodReturnType: String,
    val arrive: Boolean = false,
) {
    fun method(): String = "${className.simpleName.replaceFirstChar { it.lowercase() }}.$methodName(${arguments.joinToString(", ")})"
}

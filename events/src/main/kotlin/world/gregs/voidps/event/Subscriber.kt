package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

/**
 * Information about a method subscribing to an event
 * Used for generating Publisher classes
 */
data class Subscriber(
    val className: ClassName,
    val methodName: String,
    val parameters: List<Pair<String, String>>,
    val schema: Publisher,
    val annotationArgs: Map<String, Any>,
    val classParams: List<Pair<String, TypeName>>,
)
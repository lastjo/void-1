package world.gregs.voidps.type.sub

/**
 * Move character subscriber
 *
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Move(
    vararg val ids: String,
    val from: IntArray = [],
    val to: IntArray = [],
)

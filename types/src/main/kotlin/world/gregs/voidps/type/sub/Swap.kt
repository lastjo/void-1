package world.gregs.voidps.type.sub

/**
 * Inventory swap subscriber
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Swap(
    val fromId: String = "*",
    val fromComponent: String = "*",
    val toId: String = "*",
    val toComponent: String = "*"
)

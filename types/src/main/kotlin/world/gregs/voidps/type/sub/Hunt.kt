package world.gregs.voidps.type.sub

/**
 * Hunt subscriber
 *
 * ```kotlin
 * ```
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Hunt(
    val mode: String = "*",
    val npc: String = "*",
    val id: String = "*",
)

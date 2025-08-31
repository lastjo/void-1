package world.gregs.voidps.type.sub

/**
 * Enter area subscriber
 *
 * ```kotlin
 * ```
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Exit(
    val area: String = "*",
    val tag: String = "*",
)

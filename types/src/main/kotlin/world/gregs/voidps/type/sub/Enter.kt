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
annotation class Enter(
    val area: String = "*",
    val tag: String = "*",
)

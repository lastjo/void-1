package world.gregs.voidps.type.sub

/**
 * Object teleport subscriber
 *
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Teleport(
    val option: String,
    vararg val ids: String,
    val land: Boolean = false
)

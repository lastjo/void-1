package world.gregs.voidps.type.sub

/**
 * Object teleport subscriber
 *
 * ```kotlin
 * return -1 // cancel
 *
 * return 0 // continue
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TeleportLand(
    val type: String = "",
    vararg val ids: String,
)

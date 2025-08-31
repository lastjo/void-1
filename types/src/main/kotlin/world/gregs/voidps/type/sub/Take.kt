package world.gregs.voidps.type.sub

/**
 * Take item subscriber
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Take(
    vararg val ids: String,
)

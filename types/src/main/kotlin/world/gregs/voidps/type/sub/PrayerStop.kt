package world.gregs.voidps.type.sub

/**
 * Prayer subscriber
 *
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PrayerStop(
    vararg val ids: String,
)

package world.gregs.voidps.type.sub

/**
 * Prayer subscriber
 *
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PrayerStart(
    vararg val ids: String,
)

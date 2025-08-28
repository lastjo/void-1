package world.gregs.voidps.type.sub

/**
 * Stop timer subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStop(
    vararg val ids: String,
)

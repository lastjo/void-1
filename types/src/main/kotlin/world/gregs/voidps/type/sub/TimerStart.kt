package world.gregs.voidps.type.sub

/**
 * Start timer subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStart(
    vararg val ids: String,
)

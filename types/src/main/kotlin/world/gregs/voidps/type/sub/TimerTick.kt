package world.gregs.voidps.type.sub

/**
 * Timer tick subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerTick(
    vararg val ids: String,
)

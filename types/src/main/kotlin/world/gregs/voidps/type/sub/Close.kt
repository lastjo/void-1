package world.gregs.voidps.type.sub

/**
 * Interface close subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Close(
    vararg val ids: String,
)

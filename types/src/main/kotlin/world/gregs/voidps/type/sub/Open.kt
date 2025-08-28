package world.gregs.voidps.type.sub

/**
 * Open interface subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Open(
    vararg val ids: String,
)

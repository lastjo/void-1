package world.gregs.voidps.type.sub

/**
 * Entity spawn subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Spawn(
    vararg val ids: String,
)

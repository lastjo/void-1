package world.gregs.voidps.type.sub

/**
 * Generic subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Subscribe(
    val event: String = "*",
    vararg val ids: String,
)

package world.gregs.voidps.type.sub

/**
 * Item removed subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Removed(
    vararg val ids: String,
    val slots: IntArray = [],
    val inventory: String = "*",
)

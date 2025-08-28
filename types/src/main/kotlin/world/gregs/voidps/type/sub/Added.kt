package world.gregs.voidps.type.sub

/**
 * Item added subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Added(
    vararg val ids: String,
    val slots: IntArray = [],
    val inventory: String = "*",
)

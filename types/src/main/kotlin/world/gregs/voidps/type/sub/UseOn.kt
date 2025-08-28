package world.gregs.voidps.type.sub

/**
 * Interface or item on entity subscriber
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UseOn(
    val item: String = "*",
    val on: String = "*",
    val id: String = "*",
    val component: String = "*",
    val approach: Boolean = false
)

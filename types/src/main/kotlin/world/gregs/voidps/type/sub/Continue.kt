package world.gregs.voidps.type.sub

/**
 * Continue dialogue subscriber
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Continue(
    val id: String = "*",
    val component: String = "*",
    val option: String = "*",
)

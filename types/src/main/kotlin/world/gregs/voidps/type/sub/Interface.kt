package world.gregs.voidps.type.sub

/**
 * Interface click subscriber
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Interface(
    val option: String = "*",
    val component: String = "*",
    val id: String = "*",
)

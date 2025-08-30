package world.gregs.voidps.type.sub

/**
 * Level change subscriber
 * ```kotlin
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class LevelChange(
    vararg val skills: String,
    val from: Int = -1,
    val to: Int = -1,
    val max: Boolean = false,
)

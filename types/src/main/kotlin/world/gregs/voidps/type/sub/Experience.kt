package world.gregs.voidps.type.sub

/**
 * Grant experience subscriber
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Experience(
    vararg val skills: String,
    val blocked: Boolean = false,
)

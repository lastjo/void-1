package world.gregs.voidps.type.sub

/**
 * Special attack subscriber
 * ```kotlin
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SpecialAttack(
    val id: String = "",
    val prepare: Boolean = false
)

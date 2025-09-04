package world.gregs.voidps.type.sub

/**
 * Special attack subscriber
 * @param damage on hit
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SpecialAttack(
    val id: String = "",
    val damage: Boolean = false,
)

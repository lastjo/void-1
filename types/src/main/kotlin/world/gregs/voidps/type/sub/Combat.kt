package world.gregs.voidps.type.sub

/**
 * Combat subscriber
 *
 * The main difference is Attack is called on attack, damage after the delay.
 * CombatAttack - target damage taken
 * CombatDamage - source damage dealt
 *
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Combat(
    val weapon: String = "*",
    val type: String = "*",
    val spell: String = "*",
    val id: String = "*",
)

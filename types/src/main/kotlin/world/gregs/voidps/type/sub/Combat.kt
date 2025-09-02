package world.gregs.voidps.type.sub

import world.gregs.voidps.type.CombatStage

/**
 * Combat subscriber
 *
 * @param weapon The weapon used (for player attacks only)
 * @param type The type of attack used (melee, range, magic)
 * @param spell The type of magic spell used
 * @param stage The [CombatStage] during a combat interaction when the subscriber will be called
 *
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Combat(
    val weapon: String = "*",
    val type: String = "*",
    val spell: String = "*",
    val id: String = "*",
    val stage: Int = CombatStage.ATTACK,
)

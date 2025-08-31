package world.gregs.voidps.type.sub

/**
 * Combat subscriber
 *
 * @param weapon The weapon used (for player attacks only)
 * @param type The type of attack used (melee, range, magic)
 * @param spell The type of magic spell used
 * @param swing If the subscriber should be called on swing before any calculations have been made
 * @param afterDelay If the subscriber should be called on attack (default) or on hit (after the hit delay)
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
    val swing: Boolean = false,
    val afterDelay: Boolean = false,
)

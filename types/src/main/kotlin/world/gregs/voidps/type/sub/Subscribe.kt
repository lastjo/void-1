package world.gregs.voidps.type.sub

/**
 * Generic subscriber for events that aren't used frequently
 * and don't have a dedicated annotation.
 *
 * ```kotlin
 * @Subscribe("consume", "bandits_brew")
 * fun banditsBrew(player: Player): Boolean {
 *     return false // cancel (optional)
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Subscribe(
    val event: String = "*",
    vararg val ids: String,
)

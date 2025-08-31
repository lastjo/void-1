package world.gregs.voidps.type.sub

/**
 * Consumption subscriber
 *
 * ```kotlin
 * @Consume("beer")
 * fun beer(player: Player): Boolean {
 *     return false // return true to cancel
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Consume(
    vararg val ids: String,
)

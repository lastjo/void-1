package world.gregs.voidps.type.sub

/**
 * Interface opened subscriber
 * ```kotlin
 * @Open("bank")
 * fun bankOpen(player: Player) {
 * }
 *
 *
 * @Open("price_checker")
 * fun openPriceChecker(player: Player, id: String) {
 * }
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Open(
    vararg val ids: String,
)

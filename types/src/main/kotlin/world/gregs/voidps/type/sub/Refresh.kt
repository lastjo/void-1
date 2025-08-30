package world.gregs.voidps.type.sub

/**
 * Interface refresh subscriber
 * ```kotlin
 * @AnnotationRetention("bank")
 * fun bankRefresh(player: Player) {
 * }
 *
 *
 * @AnnotationRetention("trade_side", "trade_main")
 * fun refreshTrade(player: Player, id: String) {
 * }
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Refresh(
    vararg val ids: String,
)

package world.gregs.voidps.type.sub

/**
 * Interface closed subscriber
 * ```kotlin
 * @Close("bank")
 * fun bankClose(player: Player) {
 * }
 *
 *
 * @Close("trade_side", "trade_main")
 * fun closeTrade(player: Player, id: String) {
 * }
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Close(
    vararg val ids: String,
)

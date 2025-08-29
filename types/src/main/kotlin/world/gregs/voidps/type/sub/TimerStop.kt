package world.gregs.voidps.type.sub

/**
 * Stop timer subscriber
 *
 * ```kotlin
 * @TimerStop("teleport_block")
 * fun clearTeleblock(player: Player) {
 * }
 *
 * @TimerStop("disease")
 * fun clearDisease(character: Character, timer: String, logout: Boolean) {
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStop(
    vararg val ids: String,
)

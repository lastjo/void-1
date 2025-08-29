package world.gregs.voidps.type.sub

/**
 * Start timer subscriber
 *
 * ```kotlin
 * @TimerStart("teleport_block")
 * fun teleblock(player: Player): Int {
 *     return 50 // Interval
 * }
 *
 * @TimerStart("disease")
 * fun diseased(character: Character, timer: String, restart: Boolean): Int {
 *     return 30
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerStart(
    vararg val ids: String,
)

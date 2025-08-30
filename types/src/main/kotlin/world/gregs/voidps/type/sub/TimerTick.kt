package world.gregs.voidps.type.sub

/**
 * Timer tick subscriber
 *
 * ```kotlin
 * @TimerTick("teleport_block")
 * fun teleblock(player: Player): Int {
 *     return -1 // Next interval, -1 for same, 0 for cancel
 * }
 *
 * @TimerTick("disease")
 * fun diseaseHit(character: Character, timer: String) {
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class TimerTick(
    vararg val ids: String,
)

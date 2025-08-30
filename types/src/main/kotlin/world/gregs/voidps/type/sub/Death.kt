package world.gregs.voidps.type.sub

/**
 * Entity death subscriber
 *
 * ```kotlin
 * @Death
 * fun death(player: Player) {
 * }
 *
 * @Death("cow")
 * fun cowDeath(npc: NPC) {
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Death(
    vararg val ids: String,
)

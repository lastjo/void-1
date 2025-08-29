package world.gregs.voidps.type.sub

/**
 * Entity option subscriber
 *
 * ```kotlin
 * @Option("Talk-to", "hans")
 * fun hans(player: Player, hans: NPC) {
 *
 * }
 * ```
 *
 *
 * ```kotlin
 * @Option("Attack", approach = true)
 * fun npcAttackPlayer(character: Character, target: Player, option: String) {
 *
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Option(
    val option: String = "*",
    vararg val ids: String,
    val approach: Boolean = false,
)

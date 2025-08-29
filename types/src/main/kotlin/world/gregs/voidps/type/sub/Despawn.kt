package world.gregs.voidps.type.sub

/**
 * Entity despawn subscriber
 *
 * @see [content.entity.death.Death] as it is different from despawn
 *
 * ```kotlin
 * @Despawn
 * fun logout(player: Player): Boolean {
 *     return false
 * }
 *
 * @Despawn("cow")
 * fun cowDespawn(npc: NPC): Boolean {
 *     return false
 * }
 *
 * @Despawn
 * fun shutdown(world: World): Boolean {
 *     return false
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Despawn(
    vararg val ids: String,
)

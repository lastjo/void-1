package world.gregs.voidps.type.sub

/**
 * Entity despawn subscriber
 *
 * @see [Death] as it is different from despawn
 *
 * ```kotlin
 * @Despawn
 * fun logout(player: Player) {
 * }
 *
 * @Despawn("cow")
 * fun cowDespawn(npc: NPC) {
 * }
 *
 * @Despawn
 * fun shutdown(world: World) {
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Despawn(
    vararg val ids: String,
)

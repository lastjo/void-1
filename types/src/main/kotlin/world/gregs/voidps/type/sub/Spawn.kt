package world.gregs.voidps.type.sub

/**
 * Entity spawn subscriber
 *
 * ```kotlin
 * @Spawn
 * fun login(player: Player): Boolean {
 *     return false
 * }
 *
 * @Spawn("cow")
 * fun cowSpawn(npc: NPC): Boolean {
 *     return false
 * }
 *
 * @Spawn
 * fun startup(world: World): Boolean {
 *     return false
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Spawn(
    vararg val ids: String,
)

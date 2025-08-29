package world.gregs.voidps.type.sub

import world.gregs.voidps.type.PlayerRights

/**
 * Command subscriber
 *
 * ```kotlin
 * @Command("mypos", description = "find your tile position")
 * fun position(player: Player) {
 * }
 *
 *
 * @Command("item (item-id) [item-amount]", rights = PlayerRights.ADMIN, description = "spawn an item")
 * fun spawnItem(player: Player, content: String) {
 * }
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Command(
    vararg val ids: String,
    val description: String = "",
    val rights: Int = PlayerRights.NONE
)

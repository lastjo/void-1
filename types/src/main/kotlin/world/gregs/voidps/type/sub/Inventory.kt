package world.gregs.voidps.type.sub

/**
 * Inventory click subscriber
 *
 * ```kotlin
 * @Inventory("Bury")
 * fun buryBones(player: Player) {
 * }
 *
 *
 * @Inventory("Wear", "ring_of_stone")
 * fun stone(player: Player, item: Item, inventory: String, option: String, itemSlot: Int) {
 * }
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Inventory(
    val option: String = "*",
    val item: String = "*",
    val inventory: String = "inventory",
)

package world.gregs.voidps.type.sub

/**
 * Inventory click subscriber
 *
 * ```kotlin
 * @Interface("Toggle Accept Aid", "aid", "options)
 * fun toggleAid(player: Player) {
 * }
 *
 *
 * @Interface("View" id = "filter_buttons")
 * fun viewFilter(player: Player, id: String, component: String, option: String, optionIndex: Int, item: Item, itemSlot: Int, inventory: String) {
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

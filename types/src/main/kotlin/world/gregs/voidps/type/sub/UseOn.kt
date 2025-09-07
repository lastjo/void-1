package world.gregs.voidps.type.sub

/**
 * Interface or item on entity subscriber
 *
 * ```kotlin
 * @UseOn("redberry_pie", "thurgo")
 * suspend fun giveThurgoPie(player: Player) {
 * }
 *
 * @UseOn(id = "*_spellbook", approach = true)
 * fun castSpell(player: Player, target: Character, id: String, component: String, item: Item, itemSlot: Int, inventory: String) {
 * }
 * ```
*/
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UseOn(
    val item: String = "*",
    val on: String = "*",
    val id: String = "*",
    val component: String = "*",
    val approach: Boolean = false,
    val bidirectional: Boolean = true, // Only applies for item on item
    val arrive: Boolean = true,
)

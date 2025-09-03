package world.gregs.voidps.type.sub

/**
 * Item added subscriber
 * ```kotlin
 * @ItemAdded("logs")
 * fun foundLogs(player: Player) {
 * }
 *
 *
 * @ItemAdded("*_tiara", slots = [EquipSlot.HAT], inventory = "worn_equipment")
 * fun equipTiara(player: Player, item: Item, slot: Int, inventory: String) {
 * }
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemAdded(
    vararg val ids: String,
    val slots: IntArray = [],
    val inventory: String = "inventory",
)

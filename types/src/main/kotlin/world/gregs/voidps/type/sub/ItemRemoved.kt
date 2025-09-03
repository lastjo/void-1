package world.gregs.voidps.type.sub

/**
 * Item removed subscriber
 * ```kotlin
 * @ItemRemoved("logs")
 * fun droppedLogs(player: Player) {
 * }
 *
 *
 * @ItemRemoved("*_tiara", slots = [EquipSlot.HAT], inventory = "worn_equipment")
 * fun removeTiara(player: Player, item: Item, slot: Int, inventory: String) {
 * }
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemRemoved(
    vararg val ids: String,
    val slots: IntArray = [],
    val inventory: String = "inventory",
)

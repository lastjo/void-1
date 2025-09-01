package world.gregs.voidps.type.sub

/**
 * Inventory changed subscriber
 * ```kotlin
 * ```
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class InventorySlotChanged(
    val inventory: String = "inventory",
    val slot: Int = -1
)

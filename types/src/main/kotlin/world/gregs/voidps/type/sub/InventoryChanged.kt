package world.gregs.voidps.type.sub

/**
 * Inventory changed subscriber
 * ```kotlin
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class InventoryChanged(
    val inventory: String = "inventory",
    val slot: Int = -1
)

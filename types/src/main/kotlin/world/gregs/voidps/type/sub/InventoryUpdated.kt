package world.gregs.voidps.type.sub

/**
 * Inventory updated subscriber
 * Updated is once per transaction, Changed is every individual change
 * ```kotlin
 * ```
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class InventoryUpdated(
    val inventory: String = "inventory",
    val slot: Int = -1
)

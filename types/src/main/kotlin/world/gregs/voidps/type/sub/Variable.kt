package world.gregs.voidps.type.sub

/**
 * Variable set subscriber
 * ```kotlin
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Variable(
    vararg val keys: String,
    val from: String = "*",
    val to: String = "*",
    val fromInt: Int = -1,
    val toInt: Int = -1,
    val fromNull: Boolean = false,
    val toNull: Boolean = false,
    val fromBool: String = "*",
    val toBool: String = "*",
)

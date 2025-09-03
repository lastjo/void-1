package world.gregs.voidps.type.sub

/**
 * Variable bits subscriber
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class VariableBits(
    vararg val keys: String,
    val added: Boolean = true,
)

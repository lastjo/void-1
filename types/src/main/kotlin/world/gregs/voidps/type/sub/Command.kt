package world.gregs.voidps.type.sub

import world.gregs.voidps.type.PlayerRights

/**
 * Command subscriber
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Command(
    vararg val ids: String,
    val description: String = "",
    val rights: Int = PlayerRights.NONE
)

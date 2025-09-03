package world.gregs.voidps.type.sub

import kotlin.reflect.KClass

/**
 * Instruction subscriber
 *
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Instruction
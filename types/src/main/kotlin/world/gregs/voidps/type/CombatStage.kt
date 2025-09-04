package world.gregs.voidps.type

object CombatStage {
    /**
     * Check ability to proceed or if attack should be cancelled
     * Prepare for combat by checking resources and calculating attack style against target
     */
    const val PREPARE = 0

    /**
     * Combat has started
     */
    const val START = 1

    /**
     * A turn in a combat scenario resulting one or many hits
     * Used for calculate npc attack style
     */
    const val SWING = 2

    /**
     * Apply hit reduction and calculate delay before hit is displayed
     * @see [DAMAGE] for after the attack delay
     */
    const val ATTACK = 3

    /**
     * Damage is applied to target
     * Used for defend graphics, for effects use [ATTACK]
     */
    const val DAMAGE = 4

    /**
     * Combat movement has stopped
     */
    const val STOP = 5
}

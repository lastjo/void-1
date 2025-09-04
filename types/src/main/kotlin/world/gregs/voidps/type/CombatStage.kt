package world.gregs.voidps.type

object CombatStage {
    /**
     * Check ability to proceed or if attack should be cancelled
     */
    const val PREPARE = 0

    /**
     * Start of combat
     */
    const val START = 1

    /**
     * Calculate attack style and hit
     */
    const val SWING = 2

    /**
     * Apply hit reduction and calculate delay before hit is displayed
     */
    const val ATTACK = 3

    /**
     * Hit is applied to target
     */
    const val DAMAGE = 4

    /**
     * End of combat
     */
    const val STOP = 5
}

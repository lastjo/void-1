package world.gregs.voidps.network.login.protocol.visual.update.player

import java.util.*

enum class EquipSlot(val index: Int) {
    None(-1),
    Hat(0), // Head
    Cape(1),
    Amulet(2),
    Weapon(3),
    Chest(4),
    Shield(5),
    Legs(7),
    Hands(9),
    Feet(10),
    Ring(12),
    Ammo(13),
    ;

    companion object {
        const val HAT = 0
        const val CAPE = 1
        const val AMULET = 2
        const val WEAPON = 3
        const val CHEST = 4
        const val SHIELD = 5
        const val LEGS = 7
        const val HANDS = 9
        const val FEET = 10
        const val RING = 12
        const val AMMO = 13

        private val map = mapOf(
            "None" to None,
            "Hat" to Hat,
            "Cape" to Cape,
            "Amulet" to Amulet,
            "Weapon" to Weapon,
            "Chest" to Chest,
            "Shield" to Shield,
            "Legs" to Legs,
            "Hands" to Hands,
            "Feet" to Feet,
            "Ring" to Ring,
            "Ammo" to Ammo,
        )

        fun by(index: Int): EquipSlot = entries.firstOrNull { it.index == index } ?: None

        fun by(name: String): EquipSlot {
            val formatted = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            return map[formatted] ?: None
        }
    }
}

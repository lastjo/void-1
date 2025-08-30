package world.gregs.voidps.engine.entity.character.player.skill

enum class Skill {
    Attack,
    Defence,
    Strength,
    Constitution,
    Ranged,
    Prayer,
    Magic,
    Cooking,
    Woodcutting,
    Fletching,
    Fishing,
    Firemaking,
    Crafting,
    Smithing,
    Mining,
    Herblore,
    Agility,
    Thieving,
    Slayer,
    Farming,
    Runecrafting,
    Hunter,
    Construction,
    Summoning,
    Dungeoneering,
    ;

    fun maximum(): Int = if (this == Dungeoneering) {
        120
    } else if (this == Constitution) {
        990
    } else {
        99
    }

    companion object {
        val all = entries.toTypedArray()
        val count = all.size

        const val ATTACK = "Attack"
        const val DEFENCE = "Defence"
        const val STRENGTH = "Strength"
        const val CONSTITUTION = "Constitution"
        const val RANGED = "Ranged"
        const val PRAYER = "Prayer"
        const val MAGIC = "Magic"
        const val COOKING = "Cooking"
        const val WOODCUTTING = "Woodcutting"
        const val FLETCHING = "Fletching"
        const val FISHING = "Fishing"
        const val FIREMAKING = "Firemaking"
        const val CRAFTING = "Crafting"
        const val SMITHING = "Smithing"
        const val MINING = "Mining"
        const val HERBLORE = "Herblore"
        const val AGILITY = "Agility"
        const val THIEVING = "Thieving"
        const val SLAYER = "Slayer"
        const val FARMING = "Farming"
        const val RUNECRAFTING = "Runecrafting"
        const val HUNTER = "Hunter"
        const val CONSTRUCTION = "Construction"
        const val SUMMONING = "Summoning"
        const val DUNGEONEERING = "Dungeoneering"

        private val skills = mapOf(
            "Attack" to Attack,
            "Defence" to Defence,
            "Strength" to Strength,
            "Constitution" to Constitution,
            "Ranged" to Ranged,
            "Prayer" to Prayer,
            "Magic" to Magic,
            "Cooking" to Cooking,
            "Woodcutting" to Woodcutting,
            "Fletching" to Fletching,
            "Fishing" to Fishing,
            "Firemaking" to Firemaking,
            "Crafting" to Crafting,
            "Smithing" to Smithing,
            "Mining" to Mining,
            "Herblore" to Herblore,
            "Agility" to Agility,
            "Thieving" to Thieving,
            "Slayer" to Slayer,
            "Farming" to Farming,
            "Runecrafting" to Runecrafting,
            "Hunter" to Hunter,
            "Construction" to Construction,
            "Summoning" to Summoning,
            "Dungeoneering" to Dungeoneering,
        )

        fun of(name: String): Skill? = skills[name]
    }
}

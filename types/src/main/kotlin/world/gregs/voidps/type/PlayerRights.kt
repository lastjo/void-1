package world.gregs.voidps.type

enum class PlayerRights {
    None,
    Mod,
    Admin,
    ;

    companion object {
        const val NONE = 0
        const val MOD = 1
        const val ADMIN = 2
    }
}

package world.gregs.voidps.event

interface Condition {
    fun expression(): String
}

fun Condition?.expression(): String {
    return this?.expression() ?: ""
}
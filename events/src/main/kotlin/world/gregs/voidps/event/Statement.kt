package world.gregs.voidps.event

data class Statement(val code: String, val args: Array<Any?>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (code != other.code) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}

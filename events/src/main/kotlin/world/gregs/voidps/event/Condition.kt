package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName

interface Condition {
    val key: String
    val value: Any?
    fun statement(): Statement?
}

data class Equals(override val key: String, override val value: Any?, val explicit: Boolean = false) : Condition {
    override fun statement(): Statement? {
        return when (value) {
            is String -> when {
                value == "*" -> null
                value.startsWith("*") -> Statement("${if (explicit) "($key as String)" else key}.endsWith(%S)", arrayOf(value.removePrefix("*")))
                value.endsWith("*") -> Statement("${if (explicit) "($key as String)" else key}.startsWith(%S)", arrayOf(value.removeSuffix("*")))
                value.contains("*") || value.contains("#") -> Statement("%T(${if (explicit) "($key as String)" else key}, %S)", arrayOf(ClassName("world.gregs.voidps.engine.event", "wildcardEquals"), value))
                else -> Statement("$key == %S", arrayOf(value))
            }
            is Boolean -> if (explicit) {
                Statement("$key == %L", arrayOf(value))
            } else {
                Statement("${if (value) "" else "!"}$key", arrayOf(value))
            }
            else -> Statement("$key == %L", arrayOf(value))
        }
    }
}

data class Contains(override val key: String, override val value: Any?) : Condition {
    override fun statement(): Statement {
        return if (value is String) {
            Statement("$key.contains(%S)", arrayOf(value))
        } else {
            Statement("$key.contains(%L)", arrayOf(value))
        }
    }
}

data class GreaterThan(override val key: String, override val value: Number?) : Condition {
    override fun statement(): Statement {
        return Statement("$key > %L", arrayOf(value))
    }
}

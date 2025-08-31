package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName

sealed interface Comparator {
    val key: String
    val value: Any?
    fun statement(): Statement?
}

data class Equals(override val key: String, override val value: Any?) : Comparator {
    override fun statement(): Statement? {
        return when (value) {
            is String -> when {
                value == "*" -> null
                value.startsWith("*") -> Statement("$key.endsWith(%S)", arrayOf(value.removePrefix("*")))
                value.endsWith("*") -> Statement("$key.startsWith(%S)", arrayOf(value.removeSuffix("*")))
                value.contains("*") -> Statement("%T($key, %S)", arrayOf(ClassName("world.gregs.voidps.engine.event", "wildcardEquals"), value))
                else -> Statement("$key == %S", arrayOf(value))
            }
            is Boolean -> Statement("${if (value) "" else "!"}$key", arrayOf(value))
            else -> Statement("$key == %L", arrayOf(value))
        }
    }
}

data class Contains(override val key: String, override val value: Any?) : Comparator {
    override fun statement(): Statement {
        return if(value is String) {
            Statement("$key.contains(%S)", arrayOf(value))
        } else {
            Statement("$key.contains(%L)", arrayOf(value))
        }
    }
}

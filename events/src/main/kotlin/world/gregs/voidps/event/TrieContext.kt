package world.gregs.voidps.event

import com.squareup.kotlinpoet.TypeName

data class TrieContext(
    val name: String = "",
    val allowMultiple: Boolean = true,
    val returnType: String = "",
    val defaultReturnValue: Any = Unit,
    val suspendable: Boolean = false,
    val methodParams: List<Pair<String, TypeName>> = emptyList(),
    val checkMethod: Boolean = false,
)
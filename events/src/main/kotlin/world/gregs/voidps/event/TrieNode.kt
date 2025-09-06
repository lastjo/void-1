package world.gregs.voidps.event

import com.squareup.kotlinpoet.CodeBlock
import java.util.TreeSet

/**
 * A nested trie storing [Method]'s in order of specificity ([Method.conditions] length)
 * that can be used to [generate] valid if else statement code
 * Note: [sort] must be called after all insertions in order to generate valid code
 */
data class TrieNode(
    val condition: Condition? = null,
    var children: MutableList<TrieNode> = mutableListOf(),
    val methods: MutableSet<Method> = TreeSet(
        Comparator<Method> { a, b ->
            val specA = a.conditions.size
            val specB = b.conditions.size
            if (specA != specB) {
                specB - specA
            } else {
                a.methodName.compareTo(b.methodName)
            }
        },
    ),
    var maxDepth: Int = 0
) {

    /**
     * Generate a nested if else statement for all [children]
     */
    fun generate(context: PublisherMapping, callOnly: Boolean = false, skipElse: Boolean = false): CodeBlock {
        val block = CodeBlock.builder()
        if (condition != null) {
            val (string, args) = condition.statement()!!
            block.beginControlFlow("${if (skipElse) "" else "else "}if ($string)", *args)
        }
        var first = true
        for (child in children) {
            block.add(child.generate(context, callOnly, first))
            first = false
        }
        if (callOnly) {
            block.addStatement("return ${methods.isNotEmpty()}")
        } else {
            block.add(codeBlock(context))
        }
        if (condition != null) {
            block.endControlFlow()
        }
        return block.build()
    }

    /**
     * Build a block of [methods] with the appropriate return type.
     */
    private fun codeBlock(context: PublisherMapping): CodeBlock {
        val block = CodeBlock.builder()
        if (!context.notification) {
            val branch = methods.firstOrNull()
            if (branch == null) {
                block.addStatement("return ${if (context.returnsDefault is String) "%S" else "%L"}", context.returnsDefault)
            } else if (context.returnsDefault == Unit) {
                block.addStatement(branch.method())
                block.addStatement("return")
                return block.build()
            } else if (branch.methodReturnType == context.returnsDefault::class.qualifiedName) {
                block.addStatement("return ${branch.method()}")
            } else {
                block.addStatement(branch.method())
                block.addStatement("return ${if (context.returnsDefault is String) "%S" else "%L"}", context.returnsDefault)
            }
            return block.build()
        }
        if (context.returnsDefault == Unit) {
            for (branch in methods) {
                block.addStatement(branch.method())
            }
            block.addStatement("return")
            return block.build()
        }
        if (methods.none { it.methodReturnType == context.returnsDefault::class.qualifiedName }) {
            for (branch in methods) {
                block.addStatement(branch.method())
            }
            block.addStatement("return ${if (context.returnsDefault is String) "%S" else "%L"}", context.returnsDefault)
            return block.build()
        }
        if (methods.size == 1 && methods.all { it.methodReturnType == context.returnsDefault::class.qualifiedName }) {
            // Single return optimization
            val branch = methods.first()
            block.addStatement("return ${branch.method()}")
            return block.build()
        }
        block.addStatement("var value = ${if (context.returnsDefault is String) "%S" else "%L"}", context.returnsDefault)
        var first = true
        for (branch in methods) {
            if (branch.methodReturnType != context.returnsDefault::class.qualifiedName) {
                // Ignore differing returned values
                block.addStatement(branch.method())
                continue
            }
            if (context.returnsDefault == false) {
                // Boolean optimization
                block.addStatement("value = value || ${branch.method()}")
                continue
            }
            block.addStatement("${if (first) "var " else ""}result = ${branch.method()}")
            block.addStatement("if (result != ${if (context.returnsDefault is String) "%S" else "%L"}) {", context.returnsDefault)
            block.addStatement("    value = result")
            block.addStatement("}")
            if (first) {
                first = false
            }
        }
        block.addStatement("return value")
        return block.build()
    }

    /**
     * Insert a [method] into the trie, optionally [allowMultiple] methods per leaf node
     */
    fun insert(method: Method, allowMultiple: Boolean = true) {
        insertConditions(method.conditions, 0, method, allowMultiple)
    }

    private fun insertConditions(conditions: List<Condition>, idx: Int, method: Method, allowMultiple: Boolean) {
        if (idx >= conditions.size) {
            if (!allowMultiple && methods.size > 0) {
                error("Method ${method.method()} already exists for conditions: ${method.conditions} - ${methods.map { it.method() }}")
            }
            methods += method
            return
        }
        val cond = conditions[idx]
        val child = children.firstOrNull { it.condition == cond }
            ?: TrieNode(cond).also { children += it }
        child.insertConditions(conditions, idx + 1, method, allowMultiple)
    }

    /**
     * Computes maxDepth and rebuilds children sets in sorted order.
     * Call this once after all insertions are done.
     */
    fun sort(): Int {
        if (children.isEmpty()) {
            maxDepth = 0
            return 0
        }

        var maxChildDepth = 0
        for (child in children) {
            val childDepth = child.sort()
            maxChildDepth = maxOf(maxChildDepth, childDepth + 1)
        }
        children.sortWith(
            compareByDescending<TrieNode> { it.maxDepth }
                .thenBy { it.condition?.key ?: "" }
        )
        maxDepth = maxChildDepth
        return maxDepth
    }

    /**
     * For debugging
     */
    @Suppress("unused")
    fun asString(indent: Int = 0): String = buildString {
        appendLine("${" ".repeat(indent * 4)}Cond: ${condition?.statement()} Depth: ${maxDepth}")
        if (children.isNotEmpty()) {
            appendLine("${" ".repeat(indent * 4)}Children:")
        }
        for (child in children) {
            appendLine("${" ".repeat(indent * 4)}${child.asString(indent + 1)}")
        }
        if (methods.isNotEmpty()) {
            appendLine("${" ".repeat(indent * 4)}Methods:")
        }
        for (method in methods) {
            appendLine("${" ".repeat(indent * 4)}${method.className}.${method.methodName}()")
        }
    }
}

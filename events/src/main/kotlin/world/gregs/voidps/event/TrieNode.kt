package world.gregs.voidps.event

import com.squareup.kotlinpoet.CodeBlock
import java.util.TreeSet

/**
 * A nested trie storing [Method]'s in order of specificity ([Method.conditions] length)
 * that can be used to [generate] valid if else statement code
 */
data class TrieNode(
    val condition: Condition? = null,
    val children: MutableSet<TrieNode> = TreeSet(
        Comparator<TrieNode> { a, b ->
            when {
                a.condition != null && b.condition == null -> -1 // a comes first (has condition)
                a.condition == null && b.condition != null -> 1 // b comes first (has condition)
                else -> {
                    // Both have conditions, sort by depth of branches (more conditions = higher priority)
                    val maxDepthA = a.maxDepth()
                    val maxDepthB = b.maxDepth()
                    val depthCompare = maxDepthB.compareTo(maxDepthA) // deeper first
                    if (depthCompare != 0) {
                        depthCompare
                    } else {
                        // Same depth, sort alphabetically for consistency
                        (a.condition?.key ?: "").compareTo(b.condition?.key ?: "")
                    }
                }
            }
        },
    ),
    val methods: MutableSet<Method> = TreeSet(
        Comparator<Method> { a, b ->
            val specA = a.conditions.size
            val specB = b.conditions.size
            if (specA != specB) {
                specB - specA
            } else {
                val expected = a.conditions.joinToString { it.statement().toString() }.compareTo(b.conditions.joinToString { it.statement().toString() })
                if (expected == 0) {
                    a.method().compareTo(b.methodName)
                } else {
                    expected
                }
            }
        },
    ),
) {
    private fun maxDepth(): Int {
        if (methods.isNotEmpty()) {
            return methods.maxOf { it.conditions.size }
        }
        if (children.isEmpty()) {
            return 0
        }
        return children.maxOf { it.maxDepth() }
    }

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
}

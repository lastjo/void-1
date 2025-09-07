package world.gregs.voidps.event

import com.squareup.kotlinpoet.CodeBlock
import java.util.*

class Methods {
    val sorted: MutableMap<List<Condition>, MutableList<Method>> = TreeMap { list1, list2 ->
        // First compare by size (descending)
        val sizeComparison = list2.size.compareTo(list1.size)
        if (sizeComparison != 0) {
            return@TreeMap sizeComparison
        }

        list1.joinToString(",").compareTo(list2.joinToString(","))
    }

    fun insert(method: Method, allowMultiple: Boolean = true) {
        val methods = sorted.getOrPut(method.conditions) { mutableListOf() }
        if (!allowMultiple && methods.size > 0) {
            error("Method ${method.method()} already exists for conditions: ${method.conditions} - ${methods.map { it.method() }}")
        }
        methods.add(method)
    }

    fun generate(context: PublisherMapping, callOnly: Boolean = false): CodeBlock {
        val block = CodeBlock.builder()
        for ((conditions, methods) in sorted) {
            val args = mutableListOf<Any?>()
            val parts = mutableListOf<String>()
            for (condition in conditions) {
                val statement = condition.statement() ?: continue
                parts.add(statement.code)
                args.addAll(statement.args)
            }
            if (parts.isNotEmpty()) {
                block.beginControlFlow("if (${parts.joinToString(" && ")})", *args.toTypedArray())
            }
            if (callOnly) {
                block.addStatement("return ${methods.isNotEmpty()}")
            } else {
                block.add(codeBlock(context, methods))
            }
            if (parts.isNotEmpty()) {
                block.endControlFlow()
            }
        }
        if (callOnly) {
            block.addStatement("return false")
        } else if (context.returnsDefault !is Unit) {
            block.addStatement("return ${if (context.returnsDefault is String) "%S" else "%L"}", context.returnsDefault)
        }
        return block.build()
    }

    /**
     * Build a block of [methods] with the appropriate return type.
     */
    private fun codeBlock(context: PublisherMapping, methods: List<Method>): CodeBlock {
        val block = CodeBlock.builder()
        val method = methods.firstOrNull() ?: return block.build()
        if (method.arrive) {
            block.addStatement("player.arriveDelay()")
        }
        if (!context.notification) {
            if (method.methodReturnType == context.returnsDefault::class.qualifiedName) {
                block.addStatement("return ${method.method()}")
            } else {
                block.addStatement(method.method())
            }
            return block.build()
        }
        if (context.returnsDefault == Unit) {
            for (branch in methods) {
                block.addStatement(branch.method())
            }
            return block.build()
        }
        if (methods.none { it.methodReturnType == context.returnsDefault::class.qualifiedName }) {
            for (branch in methods) {
                block.addStatement(branch.method())
            }
            return block.build()
        }
        if (methods.size == 1 && methods.all { it.methodReturnType == context.returnsDefault::class.qualifiedName }) {
            // Single return optimization
            val method = methods.first()
            block.addStatement("return ${method.method()}")
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

}
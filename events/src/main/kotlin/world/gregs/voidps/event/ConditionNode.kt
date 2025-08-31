package world.gregs.voidps.event

import com.squareup.kotlinpoet.CodeBlock

data class ConditionNode(
    val comparator: Comparator? = null,
    val children: MutableList<ConditionNode> = mutableListOf(),
    val subscribers: MutableList<Subscriber> = mutableListOf(),
) {

    fun generate(builder: CodeBlock.Builder, schema: Publisher) {
        if (children.isEmpty()) {
            leaf(schema, builder)
            return
        }

        // Generate when statement when all children use the same key
        val firstKey = children.first().comparator?.key
        if (children.all { it.comparator is Equals && it.comparator.key == firstKey }) {
            builder.beginControlFlow("when (%L)", firstKey)
            for (child in children) {
                when (val value = child.comparator!!.value) {
                    is String -> builder.beginControlFlow("%S ->", value)
                    else -> builder.beginControlFlow("%L ->", value)
                }
                child.generate(builder, schema)
                builder.endControlFlow()
            }
            builder.addStatement("else -> {}")
            builder.endControlFlow()
        } else {
            // Fall back to if else chains when there is a mix of different keys
            for (child in children) {
                val statement = child.comparator!!.statement() ?: continue
                builder.beginControlFlow("if (${statement.code})", *statement.args)
                child.generate(builder, schema)
                builder.endControlFlow()
            }
        }

        if (subscribers.isNotEmpty()) {
            leaf(schema, builder)
        }
    }

    private fun leaf(schema: Publisher, builder: CodeBlock.Builder) {
        val cancellable = subscribers.filter { it.returnType != "kotlin.Unit" }
        if (cancellable.isNotEmpty()) {
            builder.add("handled = handled")
            for (sub in cancellable) {
                val args = arguments(sub, schema)
                val methodName = sub.className.simpleName.replaceFirstChar { it.lowercase() }
                builder.addStatement(" || %L.%L(${args.joinToString(", ")})", methodName, sub.methodName)
            }
        }
        val uncancellable = subscribers.filter { it.returnType == "kotlin.Unit" }
        if (uncancellable.isNotEmpty()) {
            if (cancellable.isNotEmpty()) {
                builder.addStatement("if (!handled) {")
            }
            for (sub in uncancellable) {
                val args = arguments(sub, schema)
                val methodName = sub.className.simpleName.replaceFirstChar { it.lowercase() }
                builder.addStatement("%L.%L(${args.joinToString(", ")})", methodName, sub.methodName)
            }
            if (cancellable.isNotEmpty()) {
                builder.addStatement("}")
            }
        }
    }

    companion object {

        /**
         * Groups [methods] into a tree of [ConditionNode]s based on [Subscriber] conditions
         * E.g.
         *      "Talk-to" - "hans"
         *                - "bob"
         *      "Teleport - "aubury"
         *                - "sedridor"
         */
        fun buildTree(schema: Publisher, methods: List<Subscriber>): ConditionNode {
            val root = ConditionNode()
            for (method in methods) {
                val comparisons = schema.comparisons(method)
                if (comparisons.isEmpty()) {
                    root.subscribers.add(method)
                    continue
                }
                for (chain in comparisons) {
                    var node = root
                    for (comparator in chain) {
                        val child = node.children.find { it.comparator == comparator }
                            ?: ConditionNode(comparator).also { node.children.add(it) }
                        node = child
                    }
                    node.subscribers.add(method)
                }
            }
            return root
        }

        /**
         * Map arguments between what the [method] wants and what the [schema] has.
         * Match by name first, fallback to type if names aren't identical.
         */
        fun arguments(method: Subscriber, schema: Publisher) = method.parameters.map { (name, type) ->
            val param = schema.parameters.firstOrNull { it.first == name }
            if (param != null && param.second == type) {
                name
            } else {
                schema.parameters.firstOrNull { it.second == type }?.first ?: error("No matching parameter $name: $type found for schema ${schema.name}")
            }
        }
    }
}

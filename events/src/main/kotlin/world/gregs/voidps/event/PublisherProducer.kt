package world.gregs.voidps.event

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import java.util.*

data class EqualsCond(val key: String, val value: Any?): Condition {
    override fun expression(): String {
        if(value == false) {
            return "!$key"
        }
        return "$key == ${if (value is String) "\"${value}\"" else value}"
    }

}

class PublisherProducer {

    fun produce(context: TrieContext, methods: List<Method>): TypeSpec {
        val builder = TypeSpec.classBuilder(context.name)
        val trie = TrieNode()
        val dependencies = TreeMap<String, ClassName>()
        for (method in methods) {
            val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
            dependencies[methodName] = method.className
            trie.insert(method, context.allowMultiple)
        }
        builder.addFunction(method(trie, context))
        if (context.checkMethod) {
            builder.addFunction(method(trie, context, callOnly = true))
        }
        builder.primaryConstructor(constructor(dependencies))
        addDependencies(builder, dependencies)

        return builder.build()
    }

    fun addDependencies(builder: TypeSpec.Builder, dependencies: Map<String, ClassName>) {
        for ((methodName, className) in dependencies) {
            builder.addProperty(
                PropertySpec.builder(methodName, className)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(methodName)
                    .build(),
            )
        }
    }

    fun method(trie: TrieNode, context: TrieContext, callOnly: Boolean = false): FunSpec {
        val builder = FunSpec.builder(if (callOnly) "has" else "publish")
        var player: String? = null
        for ((name, type) in context.methodParams) {
            if (player == null && type == PLAYER) {
                player = name
            }
            builder.addParameter(name, type)
        }
        if (callOnly) {
            builder.addCode(trie.generate(context, callOnly = true))
            builder.returns(BOOLEAN)
            return builder.build()
        }
        if (context.suspendable) {
            builder.addModifiers(KModifier.SUSPEND)
        }
        val tryCatch = CodeBlock.builder().beginControlFlow("try")
        if (player != null) {
            tryCatch.add("%L.debug { %P }\n", player, "${context.name.removeSuffix("Publisher")}[${context.methodParams.joinToString(", ") { "\$${it.first}" }}]")
        }
        builder.addCode(
            tryCatch
                .add(trie.generate(context, callOnly = false))
                .endControlFlow()
                .beginControlFlow("catch (e: %T)", Exception::class)
                .addStatement(if (player != null) "$player.warn(e) { \"Failed to publish ${context.name.removeSuffix("Publisher")}\" }" else "e.printStackTrace()")
                .addStatement("return ${if (context.defaultReturnValue is String) "%S" else "%L"}", context.defaultReturnValue)
                .endControlFlow()
                .build()
        )
        builder.returns(context.defaultReturnValue::class)
        return builder.build()
    }

    fun constructor(dependencies: Map<String, ClassName>): FunSpec {
        val constructor = FunSpec.constructorBuilder()
        for ((methodName, className) in dependencies) {
            constructor.addParameter(methodName, className)
        }
        return constructor.build()
    }
}
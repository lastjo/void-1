package world.gregs.voidps.event

import com.squareup.kotlinpoet.*

/**
 * Contains the mapping from a [Subscriber] method and [Annotation] into a generated Publisher class
 */
abstract class Publisher(
    val name: String,
    val suspendable: Boolean = false,
    val parameters: List<Pair<String, ClassName>>,
    var returnsDefault: Any = false,
    var notification: Boolean = false,
    var interaction: Boolean = false,
    val overrideMethod: String,
) {
    init {
        if (notification) {
            assert(returnsDefault is Boolean) { "Notification methods must return cancellation boolean." }
        }
    }

    abstract fun comparisons(method: Subscriber): List<List<Pair<String, Any>>>

    /**
     * Generate a publish function for a list of [Subscriber] [methods]
     */
    fun generate(methods: List<Subscriber>, check: Boolean): FunSpec {
        val funSpec = FunSpec.builder(if (check) "has" else "publish")
        if (suspendable && !check) {
            funSpec.addModifiers(KModifier.SUSPEND)
        }
        var player: String? = null
        for ((name, type) in parameters) {
            if (player == null && type == PLAYER) {
                player = name
            }
            funSpec.addParameter(name, type)
        }

        val returns = returnsDefault
        val returnSomething = returns != false
        val builder: CodeBlock.Builder
        if (notification) {
            builder = CodeBlock.builder()
            if (check) {
                // Assume any sub means existing as you can never know for certain with a notification call
                builder.addStatement("return true")
            } else {
                val root = ConditionNode.buildTree(this, methods)
                builder.addStatement("var handled = false")
                root.generate(builder, this)
                builder.addStatement("return handled")
            }
        } else {
            builder = CodeBlock.builder().beginControlFlow(if (returnSomething) "return when" else "when")
            var addedElse = false
            for (method in methods) {
                val comparisons = comparisons(method)
                addedElse = addedElse || generateStatement(builder, method, comparisons, returnSomething, check)
            }
            if (!addedElse) {
                builder.addStatement("else -> ${if (returnSomething) "" else "return "}%L", if (check) false else returns)
            }
            builder.endControlFlow()
            if (!returnSomething && !addedElse) {
                builder.addStatement("return true")
            }
        }
        if (check) {
            funSpec.addCode(builder.build())
            funSpec.returns(BOOLEAN)
        } else {
            val errorHandling = CodeBlock.builder().beginControlFlow("try")
            if (player != null) {
                errorHandling.add("%L.debug { %P }\n", player, "${name.removeSuffix("Publisher")}[${parameters.joinToString(", ") { "\$${it.first}" }}]")
            }
            funSpec.addCode(
                errorHandling
                    .add(builder.build())
                    .endControlFlow()
                    .beginControlFlow("catch (e: %T)", Exception::class)
                    .addStatement(if (player != null) "$player.warn(e) { \"Failed to publish ${name.removeSuffix("Publisher")}\" }" else "e.printStackTrace()")
                    .addStatement("return %L", returnsDefault)
                    .endControlFlow()
                    .build()
            )
            funSpec.returns(returns::class)
        }
        return funSpec.build()
    }

    private fun generateStatement(builder: CodeBlock.Builder, method: Subscriber, comparisons: List<List<Pair<String, Any>>>, returnSomething: Boolean, check: Boolean): Boolean {
        val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
        if (comparisons.isEmpty()) {
            if (check) {
                builder.addStatement("else -> ${if (returnSomething) "" else "return "}false")
            } else {
                val args = ConditionNode.arguments(method, this)
                builder.addStatement(
                    "else -> ${if (returnSomething) "" else "return "}$methodName.%L(${args.joinToString(", ")})",
                    method.methodName
                )
            }
            return true
        }
        for (comparison in comparisons) {
            // If statement
            for (i in comparison.indices) {
                if (i > 0) {
                    builder.add(" && ")
                }
                val (key, value) = comparison[i]
                when (value) {
                    is String -> when {
                        value == "*" -> continue
                        value.startsWith("*") -> builder.add("$key.endsWith(%S)", value.removePrefix("*"))
                        value.endsWith("*") -> builder.add("$key.startsWith(%S)", value.removeSuffix("*"))
                        value.contains("*") -> builder.add("%T($key, %S)", ClassName("world.gregs.voidps.engine.event", "wildcardEquals"), value)
                        else -> builder.add("$key == %S", value)
                    }
                    is Boolean -> builder.add("${if (value) "" else "!"}$key", value)
                    else -> builder.add("$key == %L", value)
                }
            }
            if (check) {
                builder.addStatement(" -> {}")
            } else {
                val args = ConditionNode.arguments(method, this)
                builder.addStatement(
                    " -> $methodName.%L(${args.joinToString(", ")})",
                    method.methodName
                )
            }
        }
        return false
    }
}

internal val PLAYER = ClassName("world.gregs.voidps.engine.entity.character.player", "Player")
internal val CHARACTER = ClassName("world.gregs.voidps.engine.entity.character.", "Character")
internal val NPC = ClassName("world.gregs.voidps.engine.entity.character.npc", "NPC")
internal val OBJECT = ClassName("world.gregs.voidps.engine.entity.obj", "GameObject")
internal val FLOOR_ITEM = ClassName("world.gregs.voidps.engine.entity.item.floor", "FloorItem")
internal val ITEM = ClassName("world.gregs.voidps.engine.entity.item", "Item")
internal val WORLD = ClassName("world.gregs.voidps.engine.entity", "World")


package world.gregs.voidps.event

import com.squareup.kotlinpoet.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Contains the mapping from a [Subscriber] method and [Annotation] into a generated Publisher class
 * @param required used to look up the schema based on the parameters given
 */
abstract class Publisher(
    val name: String,
    val suspendable: Boolean = false,
    val parameters: List<Pair<String, TypeName>>,
    val required: List<TypeName>,
    var returnsDefault: Any = false,
    var notification: Boolean = false,
    var cancellable: Boolean = false,
    var interaction: Boolean = false,
    val methodName: String,
    val checkMethodName: String? = null,
) {

    /*
        Takes in annotated methods
        for each annotation
            For each annotated method
                generate list of conditions -> method call with arguments
                insert into tree
            parse tree into code
                if notification
                else


         for each method
         add to publishers
     */
    constructor(function: KFunction<*>, hasFunction: KFunction<*>? = null, notification: Boolean = false, cancellable: Boolean = false, returnsDefault: Any? = null) : this(
        name = "${function.name.replaceFirstChar { it.uppercase() }}Publisher",
        parameters = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map {
            val typeName = it.type.asTypeName()
            it.name!! to typeName
        },
        required = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.filter { !it.isOptional }.map { it.type.asTypeName() },
        returnsDefault = returnsDefault ?: when (function.returnType.asTypeName()) {
            STRING -> ""
            INT -> -1
            else -> false
        },
        methodName = function.name,
        checkMethodName = hasFunction?.name,
        suspendable = function.isSuspend,
        notification = notification,
        interaction = hasFunction != null,
        cancellable = cancellable,
    ) {
        if (hasFunction != null) {
            assert(hasFunction.returnType.asTypeName() == BOOLEAN) { "Publisher check method '${hasFunction.name}' must return a Boolean." }
            assert(!hasFunction.isSuspend) { "Publisher check method '${hasFunction.name}' cannot be suspendable." }

            val expected = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.name }.toSet()
            val actual = hasFunction.parameters.filter { it.kind == KParameter.Kind.VALUE }.map { it.name }.toSet()
            assert(expected.size == actual.size) { "Publisher check method '${hasFunction.name}' must have all the same parameters as publish function '${function.name}'." }
            assert(actual.containsAll(expected)) { "Publisher check method '${hasFunction.name}' must have all the same parameters as publish function '${function.name}'." }
        }
    }

    init {
        if (cancellable && notification) {
            assert(returnsDefault != true) { "You can't return true by default with a cancellable notification." }
        }
        if (interaction) {
            assert(parameters.any { it.first == "approach" }) { "Interactions must contain an approach/operate toggle." }
        }
    }

    /**
     * Map arguments between what the [method] wants and what the [schema] has.
     * Match by name first, fallback to type if names aren't identical.
     */
    open fun arguments(method: Subscriber): List<List<String>> {
        val count = mutableMapOf<TypeName, Int>()
        for ((_, type) in parameters) {
            count[type] = count.getOrDefault(type, 0) + 1
        }
        return match(method, count)
    }

    open fun match(method: Subscriber, count: MutableMap<TypeName, Int>): List<List<String>> {
        return listOf(method.parameters.map { (name, type) ->
            if (count.getOrDefault(type, 0) > 1) {
                // match by name
                parameters.firstOrNull { it.first == name }
            } else {
                // match by type
                parameters.firstOrNull { it.second == type }
            }?.first ?: error("Expected parameter [${parameters.filter { it.second == type }.joinToString(", ") { it.first }}] for ${method.methodName}($name: ${type.toString().substringAfter(".")}) in ${method.className}.")
        })
    }

    abstract fun comparisons(method: Subscriber): List<List<Comparator>>

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
        val returnSomething = cancellable || returns != false
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
            val methodComparisons = methods.map { it to comparisons(it) }
            for ((method, comparisons) in methodComparisons.filter { it.second.isNotEmpty() }) {
                generateStatement(builder, method, comparisons, check, returnSomething)
            }
            val remaining = methodComparisons.filter { it.second.isEmpty() }
            for ((method, _) in remaining) {
                addedElse = true
                generateElse(check, builder, returnSomething, method)
            }
            if (!addedElse) {
                if (returns is String) {
                    builder.addStatement("else -> ${if (returnSomething) "" else "return "}%S", if (check) false else returns)
                } else {
                    builder.addStatement("else -> ${if (returnSomething) "" else "return "}%L", if (check) false else returns)
                }
            }
            builder.endControlFlow()
            if (!returnSomething && !addedElse) {
                if (returns is String) {
                    builder.addStatement("return \"\"")
                } else {
                    builder.addStatement("return true")
                }
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
            errorHandling
                .add(builder.build())
                .endControlFlow()
                .beginControlFlow("catch (e: %T)", Exception::class)
                .addStatement(if (player != null) "$player.warn(e) { \"Failed to publish ${name.removeSuffix("Publisher")}\" }" else "e.printStackTrace()")
            if (returnsDefault is String) {
                errorHandling.addStatement("return %S", returnsDefault)
            } else {
                errorHandling.addStatement("return %L", returnsDefault)
            }
            funSpec.addCode(
                errorHandling
                    .endControlFlow()
                    .build(),
            )
            funSpec.returns(returns::class)
        }
        return funSpec.build()
    }

    private fun generateElse(check: Boolean, builder: CodeBlock.Builder, returnSomething: Boolean, method: Subscriber) {
        if (check) {
            builder.addStatement("else -> ${if (returnSomething) "" else "return "}false")
        } else {
            val list = arguments(method)
            val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
            if (!returnSomething && method.returnType == "kotlin.Unit") {
                builder.addStatement("else -> {")
                for (args in list) {
                    builder.addStatement("  $methodName.%L(${args.joinToString(", ")})", method.methodName)
                }
                if (method.schema.returnsDefault is String) {
                    builder.addStatement("  return %S", method.schema.returnsDefault)
                } else {
                    builder.addStatement("  return %L", method.schema.returnsDefault)
                }
                builder.addStatement("}")
            } else {
                val args = list.first()
                assert(list.size < 2) { "Multi else return type list args not supported" }
                builder.addStatement(
                    "else -> ${if (returnSomething) "" else "return "}$methodName.%L(${args.joinToString(", ")})",
                    method.methodName,
                )
            }
        }
    }

    private fun generateStatement(builder: CodeBlock.Builder, method: Subscriber, comparisons: List<List<Comparator>>, check: Boolean, returnSomething: Boolean) {
        val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
        for (comparison in comparisons) {
            // If statement
            for (i in comparison.indices) {
                if (i > 0) {
                    builder.add(" && ")
                }
                val statement = comparison[i].statement() ?: continue
                builder.add(statement.code, *statement.args)
            }
            builder.add(" -> ")
            if (check) {
                builder.addStatement("{}")
            } else {
                val list = arguments(method)
                if (returnSomething && method.returnType == "kotlin.Unit") {
                    builder.addStatement("{")
                    for (args in list) {
                        builder.addStatement("  $methodName.%L(${args.joinToString(", ")})", method.methodName)
                    }
                    if (method.schema.returnsDefault is String) {
                        builder.addStatement("  %S", method.schema.returnsDefault)
                    } else {
                        builder.addStatement("  %L", method.schema.returnsDefault)
                    }
                    builder.addStatement("}")
                } else {
                    assert(list.size < 2) { "Multi else return type list args not supported" }
                    for (args in list) {
                        builder.addStatement("$methodName.%L(${args.joinToString(", ")})", method.methodName,)
                    }
                }
            }
        }
    }
}

internal val PLAYER = ClassName("world.gregs.voidps.engine.entity.character.player", "Player")
internal val CHARACTER = ClassName("world.gregs.voidps.engine.entity.character.", "Character")
internal val NPC = ClassName("world.gregs.voidps.engine.entity.character.npc", "NPC")
internal val OBJECT = ClassName("world.gregs.voidps.engine.entity.obj", "GameObject")
internal val FLOOR_ITEM = ClassName("world.gregs.voidps.engine.entity.item.floor", "FloorItem")
internal val ITEM = ClassName("world.gregs.voidps.engine.entity.item", "Item")
internal val WORLD = ClassName("world.gregs.voidps.engine.entity", "World")

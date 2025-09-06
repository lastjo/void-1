package world.gregs.voidps.event

import com.squareup.kotlinpoet.*
import world.gregs.voidps.engine.entity.character.player.Player
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf

/**
 * Contains the mapping from a [Subscriber] method and [Annotation] into a number of [Method]s
 * @param required used to look up the schema based on the parameters given
 */
abstract class PublisherMapping(
    val name: String,
    val suspendable: Boolean = false,
    val parameters: List<Pair<String, KType>>,
    val required: List<KType>,
    var returnsDefault: Any = false,
    var notification: Boolean = false,
    var cancellable: Boolean = false,
    var interaction: Boolean = false,
    val methodName: String,
    val checkMethodName: String? = null,
) {

    constructor(function: KFunction<*>, hasFunction: KFunction<*>? = null, notification: Boolean = false, cancellable: Boolean = false, returnsDefault: Any? = null) : this(
        name = "${function.name.replaceFirstChar { it.uppercase() }}Publisher",
        parameters = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.map {
            it.name!! to it.type
        },
        required = function.parameters.filter { it.kind == KParameter.Kind.VALUE }.filter { !it.isOptional }.map { it.type },
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

    open fun methods(subscriber: Subscriber): List<Method> {
        val conditions = conditions(subscriber)
        if (conditions.isEmpty()) {
            error("Conditions must not be empty $this.")
        }
        return conditions.map { comparisons ->
            Method(
                conditions = comparisons,
                suspendable = suspendable,
                className = subscriber.className,
                methodName = subscriber.methodName,
                arguments = matchNames(subscriber.parameters, subscriber),
                methodReturnType = subscriber.returnType,
            )
        }
    }

    /**
     * Map arguments between what the [method] wants and what the [schema] has.
     * Match first by name and type exactly, if not then by first type, and finally by subtype
     */
    fun matchNames(names: List<Pair<String, KClass<*>>>, method: Subscriber) = names.map { (name, type) ->
        parameters.firstOrNull { it.first == name && it.second == type.createType(nullable = it.second.isMarkedNullable) }?.first
            ?: parameters.firstOrNull { it.second == type.createType(nullable = it.second.isMarkedNullable) }?.first
            ?: parameters.firstOrNull { it.second.isSupertypeOf(type.createType(nullable = it.second.isMarkedNullable)) }?.first ?: error("Expected parameter [${parameters.filter { it.second.asTypeName() == type.asTypeName() }.joinToString(", ") { it.first }}] for ${method.methodName}($name: ${type.toString().substringAfter(".")}) in ${method.className}.")
    }

    abstract fun conditions(method: Subscriber): List<List<Condition>>

    fun produce(methods: List<Method>): TypeSpec {
        val builder = TypeSpec.classBuilder(name)
        val trie = TrieNode()
        val dependencies = TreeMap<String, ClassName>()
        for (method in methods) {
            val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
            dependencies[methodName] = method.className
            trie.insert(method, notification)
        }
        trie.sort()
        builder.addFunction(method(trie))
        if (interaction) {
            builder.addFunction(method(trie, callOnly = true))
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

    fun method(trie: TrieNode, callOnly: Boolean = false): FunSpec {
        val builder = FunSpec.builder(if (callOnly) "has" else "publish")
        var player: String? = null
        for ((name, type) in parameters) {
            if (player == null && type.classifier as KClass<*> == Player::class) {
                player = name
            }
            builder.addParameter(name, type.asTypeName())
        }
        if (callOnly) {
            builder.addCode(trie.generate(this, callOnly = true, topLevel = true))
            builder.returns(BOOLEAN)
            return builder.build()
        }
        if (suspendable) {
            builder.addModifiers(KModifier.SUSPEND)
        }
        val tryCatch = CodeBlock.builder().beginControlFlow("try")
        if (player != null) {
            tryCatch.add("%L.debug { %P }\n", player, "${name.removeSuffix("Publisher")}[${parameters.joinToString(", ") { "\$${it.first}" }}]")
        }
//        tryCatch.add("println(%P)\n", "${name.removeSuffix("Publisher")}[${parameters.joinToString(", ") { "\$${it.first}" }}]")
        builder.addCode(
            tryCatch
                .add(trie.generate(this, callOnly = false, topLevel = true))
                .endControlFlow()
                .beginControlFlow("catch (e: %T)", Exception::class)
                .addStatement(if (player != null) "$player.warn(e) { \"Failed to publish ${name.removeSuffix("Publisher")}\" }" else "e.printStackTrace()")
                .addStatement("return ${if (returnsDefault is String) "%S" else "%L"}", returnsDefault)
                .endControlFlow()
                .build(),
        )
        builder.returns(returnsDefault::class)
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

internal val PLAYER = ClassName("world.gregs.voidps.engine.entity.character.player", "Player")
internal val CHARACTER = ClassName("world.gregs.voidps.engine.entity.character.", "Character")
internal val NPC = ClassName("world.gregs.voidps.engine.entity.character.npc", "NPC")
internal val OBJECT = ClassName("world.gregs.voidps.engine.entity.obj", "GameObject")
internal val FLOOR_ITEM = ClassName("world.gregs.voidps.engine.entity.item.floor", "FloorItem")
internal val ITEM = ClassName("world.gregs.voidps.engine.entity.item", "Item")
internal val WORLD = ClassName("world.gregs.voidps.engine.entity", "World")

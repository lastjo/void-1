package world.gregs.voidps.event

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import world.gregs.voidps.engine.event.Publishers
import java.util.*

/**
 * Takes [Subscriber]s marked with annotations and generates:
 * 1. [Publisher] classes filled with if statements in order to publish events
 * 2. Publishers implementation with methods to call each individual Publisher class
 */
class PublisherProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val schemas: Map<String, List<Publisher>>,
    private val superclass: ClassName,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val start = System.currentTimeMillis()
        val annotations = schemas.keys
        val mainClass = TypeSpec.classBuilder("PublishersImpl")
        mainClass.superclass(superclass)
        val allScripts = mutableMapOf<String, ClassName>()
        val allDependencies = TreeMap<TypeName, String>()
        allDependencies[Publishers::class.asTypeName()] = "this"
        var total = 0
        var count = 0
        for (annotation in annotations) {
            val symbols = resolver.getSymbolsWithAnnotation(annotation)
            val subscriptions = symbols.filterIsInstance<KSFunctionDeclaration>()
                .flatMap { fn -> extractSubscription(annotation, fn) }
                .groupBy { it.schema }
            if (subscriptions.isEmpty()) {
                continue
            }
            for ((schema, subs) in subscriptions) {
                val methods = subs.flatMap { schema.methods(it) }
                total += methods.size
                count++

                val properties = extractProperties(subs, allScripts, allDependencies)
                for (property in properties) {
                    mainClass.addProperty(property)
                }

                val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", schema.name)
                fileSpec.addType(schema.produce(methods))

                // Create variables for each publisher
                val fieldName = schema.name.replaceFirstChar { it.lowercase() }
                mainClass.addProperty(property(subs, fieldName, schema))
                mainClass.addFunction(overrideMethod(schema, fieldName, check = false))
                if (schema.interaction) {
                    mainClass.addFunction(overrideMethod(schema, fieldName, check = true))
                }
                try {
                    fileSpec.build().writeTo(codeGenerator, Dependencies(false))
                } catch (e: FileAlreadyExistsException) {
                    e.printStackTrace()
                }
            }
        }
        if (count == 0) {
            logger.info("Not symbols found; skipping.")
            return emptyList()
        }

        // Add all dependencies to main constructor
        mainClass.primaryConstructor(constructor(allDependencies))
        // Statistics
        mainClass.addProperty(PropertySpec.builder("subscriptions", INT).initializer(total.toString()).build())
        mainClass.addProperty(PropertySpec.builder("publishers", INT).initializer(count.toString()).build(),)

        // Save main file
        val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", "PublishersImpl")
        fileSpec.addType(mainClass.build())
        try {
            fileSpec.build().writeTo(codeGenerator, Dependencies(false))
        } catch (e: FileAlreadyExistsException) {
            e.printStackTrace()
        }
        logger.info("PublisherProcessor took ${System.currentTimeMillis() - start} ms")
        return emptyList()
    }

    private fun property(subs: List<Subscriber>, fieldName: String, schema: Publisher): PropertySpec {
        val deps = subs.map { it.className.simpleName.replaceFirstChar { c -> c.lowercase() } }
            .distinct()
            .sorted()
            .joinToString(", ")
        return PropertySpec.builder(fieldName, ClassName("world.gregs.voidps.engine.script", schema.name))
            .addModifiers(KModifier.PRIVATE)
            .initializer("${schema.name}($deps)")
            .build()
    }

    private fun constructor(allDependencies: TreeMap<TypeName, String>): FunSpec {
        val constructor = FunSpec.constructorBuilder()
        for ((type, param) in allDependencies) {
            if (param == "this") {
                continue
            }
            constructor.addParameter(param, type)
        }
        return constructor.build()
    }

    private fun extractProperties(subs: List<Subscriber>, scripts: MutableMap<String, ClassName>, dependencies: MutableMap<TypeName, String>): List<PropertySpec> {
        val properties = mutableListOf<PropertySpec>()
        for (method in subs) {
            val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
            if (scripts.putIfAbsent(methodName, method.className) == null) {
                // Add all params to publisher classes
                for ((_, type) in method.classParams) {
                    val name = (type as ClassName).simpleName.replace("NPC", "Npc").replaceFirstChar { it.lowercase() }
                    dependencies.putIfAbsent(type, name)
                }
                // Initialize scripts classes as variables with injected params
                val classParams = method.classParams.joinToString(", ") {
                    dependencies.getValue(it.second)
                }
                properties.add(
                    PropertySpec.builder(methodName, method.className)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("${method.className.simpleName}($classParams)")
                        .build()
                )
            }
        }
        return properties
    }

    private fun overrideMethod(schema: Publisher, fieldName: String, check: Boolean): FunSpec {
        val method = FunSpec.builder(if (check) schema.checkMethodName!! else schema.methodName)
            .addModifiers(KModifier.OVERRIDE)
        if (schema.suspendable && !check) {
            method.addModifiers(KModifier.SUSPEND)
        }
        for ((name, type) in schema.parameters) {
            method.addParameter(name, type)
        }
        if (check) {
            method.addStatement("return $fieldName.has(${schema.parameters.joinToString(", ") { it.first }})").returns(BOOLEAN)
        } else {
            method.addStatement("return $fieldName.publish(${schema.parameters.joinToString(", ") { it.first }})").returns(schema.returnsDefault::class)
        }
        return method.build()
    }

    /**
     * Looks up the [Publisher] schema by matching the subscribing methods [args] against the [annotation]
     * list of [Publisher.required] args
     */
    fun findSchema(annotation: String, args: List<Pair<String, TypeName>>): Publisher? {
        val publishers = schemas[annotation] ?: return null
        for (publisher in publishers) {
            if (publisher.required.isEmpty()) {
                return publisher
            }
            var i = 0
            for ((_, type) in args) {
                val suffix = publisher.required[i]
                if (type == suffix && ++i == publisher.required.size) {
                    return publisher
                }
            }
        }
        return null
    }

    /**
     * Collect suspension, return type and parameters information about a given function [fn],
     * and check they don't conflict with the expected schema.
     */
    private fun extractSubscription(annotationName: String, fn: KSFunctionDeclaration): List<Subscriber> {
        val parentClass = fn.parentDeclaration as? KSClassDeclaration ?: return emptyList()
        val list = mutableListOf<Subscriber>()
        for (annotation in fn.annotations) {
            if (annotation.annotationType.resolve().declaration.qualifiedName?.asString() != annotationName) {
                continue
            }
            val annoType = annotation.annotationType.resolve().declaration.qualifiedName?.asString() ?: continue
            val args = annotation.arguments.associate { it.name?.asString().orEmpty() to it.value!! }
            val params = fn.parameters.map { it.name!!.getShortName() to it.type.resolve().toTypeName() }
            val schema = findSchema(annoType, params) ?: error("Unable to find required $annotation parameters for ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()}.")
            if (fn.modifiers.contains(Modifier.SUSPEND) && !schema.suspendable) {
                error("Method ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()} cannot be suspendable.")
            }
            val returnType = fn.returnType!!.resolve().declaration.qualifiedName!!.asString()
            if (schema.notification) {
                if (!schema.cancellable && returnType != "kotlin.Unit") {
                    error("Method ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()} is not cancellable notification so cannot have a return type. (returns ${returnType})")
                } else if (returnType != "kotlin.Unit" && returnType != "kotlin.Boolean") {
                    error("Method ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()} is a cancellable notification so must return a Boolean or nothing. (returns ${returnType})")
                }
            } else if (returnType != "kotlin.Unit" && returnType != schema.returnsDefault::class.qualifiedName) {
                error("Method ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()} must return a ${schema.returnsDefault::class.simpleName}. (returns ${returnType})")
            }
            val classParams: List<Pair<String, TypeName>> = parentClass.primaryConstructor?.parameters?.map { param ->
                val name = param.name?.asString() ?: error("Unnamed class param in ${parentClass.qualifiedName?.asString()}")
                name to param.type.resolve().toTypeName()
            } ?: emptyList()
            list.add(
                Subscriber(
                    className = parentClass.toClassName(),
                    methodName = fn.simpleName.asString(),
                    parameters = params,
                    schema = schema,
                    annotationArgs = args,
                    classParams = classParams,
                    returnType = returnType
                )
            )
        }
        return list
    }
}

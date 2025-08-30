package world.gregs.voidps.event

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
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
        resolve(resolver)
        logger.info("PublisherProcessor took ${System.currentTimeMillis() - start} ms")
        return emptyList()
    }

    private fun resolve(resolver: Resolver) {
        val annotations = schemas.keys
        val mainClass = TypeSpec.classBuilder("PublishersImpl")
        mainClass.superclass(superclass)
        val allScripts = mutableMapOf<String, ClassName>()
        val allDependencies = TreeMap<TypeName, String>()
        var total = 0
        var count = 0
        for (annotation in annotations) {
            val symbols = resolver.getSymbolsWithAnnotation(annotation)
            val subscriptions = symbols.filterIsInstance<KSFunctionDeclaration>()
                .mapNotNull { fn -> extractSubscription(annotation, fn) }
                .groupBy { it.schema }
            if (subscriptions.isEmpty()) {
                continue
            }
            for ((schema, methods) in subscriptions) {
                total += methods.size
                count++
                // Create a class per schema
                val classBuilder = TypeSpec.classBuilder(schema.name)
                val constructor = FunSpec.constructorBuilder()
                val dependencies = TreeMap<String, ClassName>()
                for (method in methods) {
                    val methodName = method.className.simpleName.replaceFirstChar { it.lowercase() }
                    dependencies[methodName] = method.className
                    if (allScripts.putIfAbsent(methodName, method.className) == null) {
                        // Add all params to publisher classes
                        for ((param, type) in method.classParams) {
                            allDependencies.putIfAbsent(type, param)
                        }
                        // Initialize scripts classes as variables with injected params
                        val classParams = method.classParams.joinToString(", ") { allDependencies.getValue(it.second) }
                        mainClass.addProperty(
                            PropertySpec.builder(methodName, method.className)
                                .addModifiers(KModifier.PRIVATE)
                                .initializer("${method.className.simpleName}($classParams)")
                                .build(),
                        )
                    }
                }
                // Add dependencies to class constructor
                for ((methodName, className) in dependencies) {
                    constructor.addParameter(methodName, className)
                    classBuilder.addProperty(
                        PropertySpec.builder(methodName, className)
                            .addModifiers(KModifier.PRIVATE)
                            .initializer(methodName)
                            .build(),
                    )
                }
                classBuilder.primaryConstructor(constructor.build())
                if (schema.interaction) {
                    classBuilder.addFunction(schema.generate(methods, check = true))
                }
                classBuilder.addFunction(schema.generate(methods, check = false))
                val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", schema.name)
                fileSpec.addType(classBuilder.build())

                // Create variables for each publisher
                val deps = methods.map { it.className.simpleName.replaceFirstChar { c -> c.lowercase() } }
                    .distinct()
                    .sorted()
                    .joinToString(", ")
                val fieldName = schema.name.replaceFirstChar { it.lowercase() }
                mainClass.addProperty(
                    PropertySpec.builder(
                        fieldName,
                        ClassName("world.gregs.voidps.engine.script", schema.name),
                    )
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("${schema.name}($deps)")
                        .build(),
                )
                if (schema.methodName != "") {
                    mainClass.addFunction(overrideMethod(schema, fieldName, check = false))
                    if (schema.interaction) {
                        mainClass.addFunction(overrideMethod(schema, fieldName, check = true))
                    }
                }
                try {
                    fileSpec.build().writeTo(codeGenerator, Dependencies(false))
                } catch (e: FileAlreadyExistsException) {
                    e.printStackTrace()
                }
            }
        }
        // Add all dependencies to main constructor
        val constructor = FunSpec.constructorBuilder()
        for ((type, param) in allDependencies) {
            constructor.addParameter(param, type)
        }
        mainClass.primaryConstructor(constructor.build())
        mainClass.addProperty(
            PropertySpec.builder("subscriptions", INT)
                .initializer(total.toString())
                .build(),
        )
        mainClass.addProperty(
            PropertySpec.builder("publishers", INT)
                .initializer(count.toString())
                .build(),
        )

        // Save main file
        val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", "PublishersImpl")
        fileSpec.addType(mainClass.build())
        try {
            fileSpec.build().writeTo(codeGenerator, Dependencies(false))
        } catch (e: FileAlreadyExistsException) {
            e.printStackTrace()
        }
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
    fun findSchema(annotation: String, args: List<Pair<String, String>>): Publisher {
        val publishers = schemas[annotation] ?: error("No schema found for annotation: $annotation.")
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
        error("Unable to find required parameters for $annotation subscription.")
    }

    /**
     * Collect suspension, return type and parameters information about a given function [fn],
     * and check they don't conflict with the expected schema.
     */
    private fun extractSubscription(annotationName: String, fn: KSFunctionDeclaration): Subscriber? {
        val parentClass = fn.parentDeclaration as? KSClassDeclaration ?: return null
        val annotation = fn.annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName } ?: return null
        val annoType = annotation.annotationType.resolve().declaration.qualifiedName?.asString() ?: return null

        val args = annotation.arguments.associate { it.name?.asString().orEmpty() to it.value!! }
        val params = fn.parameters.map { it.name!!.getShortName() to it.type.resolve().declaration.simpleName.asString() }
        val schema = findSchema(annoType, params)
        if (fn.modifiers.contains(Modifier.SUSPEND) && !schema.suspendable) {
            error("Method ${parentClass.simpleName.asString()}.${fn.simpleName.asString()} cannot be suspendable.")
        }
        val returnType = fn.returnType?.resolve()?.declaration?.qualifiedName?.asString()
        if ((schema.returnsDefault != false || schema.notification) && returnType != schema.returnsDefault::class.qualifiedName) {
            error("Method ${fn.simpleName.asString()} in ${parentClass.qualifiedName?.asString()} ${if (schema.notification) "is a notification so " else ""}must return a ${schema.returnsDefault::class.simpleName}.")
        }
        val classParams: List<Pair<String, TypeName>> = parentClass.primaryConstructor?.parameters?.map { param ->
            val name = param.name?.asString() ?: error("Unnamed class param in ${parentClass.qualifiedName?.asString()}")
            name to param.type.resolve().toTypeName()
        } ?: emptyList()
        return Subscriber(
            className = parentClass.toClassName(),
            methodName = fn.simpleName.asString(),
            parameters = params,
            schema = schema,
            annotationArgs = args,
            classParams = classParams,
        )
    }
}

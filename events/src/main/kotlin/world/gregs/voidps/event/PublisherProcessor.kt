package world.gregs.voidps.event

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.*

/**
 * Takes [Subscriber]s marked with annotations and generates [Publisher] classes filled with if statements in order to publish events
 */
class PublisherProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val schemas: Map<String, List<Pair<List<String>, Publisher>>>
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
        val allScripts = mutableMapOf<String, ClassName>()
        val allDependencies = TreeMap<TypeName, String>()
        for (annotation in annotations) {
            val symbols = resolver.getSymbolsWithAnnotation(annotation)
            val subscriptions = symbols.filterIsInstance<KSFunctionDeclaration>()
                .mapNotNull { fn -> extractSubscription(annotation, fn) }
                .groupBy { it.schema }
            if (subscriptions.isEmpty()) {
                continue
            }
            for ((schema, methods) in subscriptions) {
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
                                .build()
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
                            .build()
                    )
                }
                classBuilder.primaryConstructor(constructor.build())
                classBuilder.addFunction(schema.generate(methods))
                val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", schema.name)
                fileSpec.addType(classBuilder.build())

                // Create variables for each publisher
                val deps = methods.map { it.className.simpleName.replaceFirstChar { c -> c.lowercase() } }
                    .distinct()
                    .sorted()
                    .joinToString(", ")
                mainClass.addProperty(
                    PropertySpec.builder(
                        schema.name.replaceFirstChar { it.lowercase() },
                        ClassName("world.gregs.voidps.engine.script", schema.name)
                    )
                        .initializer("${schema.name}($deps)")
                        .build()
                )
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
            mainClass.addProperty(
                PropertySpec.builder(param, type)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(param)
                    .build()
            )
        }
        mainClass.primaryConstructor(constructor.build())

        // Save main file
        val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", "PublishersImpl")
        fileSpec.addType(mainClass.build())
        try {
            fileSpec.build().writeTo(codeGenerator, Dependencies(false))
        } catch (e: FileAlreadyExistsException) {
            e.printStackTrace()
        }
    }

    /**
     * Looks up the [Publisher] schema by matching the subscribing methods [args] against the [annotation]
     * list of required args
     */
    fun findSchema(annotation: String, args: List<Pair<String, String>>): Publisher {
        val list = schemas[annotation] ?: error("No schema found for annotation: $annotation.")
        for ((required, publisher) in list) {
            var i = 0
            for ((_, type) in args) {
                val suffix = required[i]
                if (type == suffix && ++i == required.size) {
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
        if (schema.returnsDefault != false && returnType != "kotlin.Unit" && returnType != schema.returnsDefault::class.qualifiedName) {
            error("Method ${parentClass.simpleName.asString()}.${fn.simpleName.asString()} must return ${schema.returnsDefault::class.simpleName}.")
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

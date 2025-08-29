package world.gregs.voidps.script

import com.github.michaelbull.logging.InlineLogger
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import world.gregs.voidps.type.Script

class ScriptProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val start = System.currentTimeMillis()
        val symbols = resolver.getSymbolsWithAnnotation(Script::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
        if (symbols.none()) {
            logger.warn("No symbols found; skipping scripts")
            return emptyList()
        }
        val builder = FunSpec.builder("load")
            .addStatement("val start = System.currentTimeMillis()")

        var count = 0
        for (symbol in symbols) {
            builder.addStatement("%T()", symbol.toClassName())
            count++
        }
        builder.addStatement("logger.info { \"Loaded $count scripts in \${System.currentTimeMillis() - start}ms\" }")
        val fileSpec = FileSpec.builder("world.gregs.voidps.engine.script", "Scripts")
            .addType(
                TypeSpec.objectBuilder("Scripts")
                    .addProperty(
                        PropertySpec.builder("logger", InlineLogger::class)
                            .initializer("InlineLogger()")
                            .addModifiers(KModifier.PRIVATE)
                            .build(),
                    )
                    .addFunction(builder.build())
                    .build(),
            )
            .build()
        try {
            val dependencies = Dependencies(
                aggregating = false,
                sources = resolver.getAllFiles().toList().toTypedArray(),
            )
            fileSpec.writeTo(codeGenerator, dependencies)
        } catch (exist: FileAlreadyExistsException) {
            exist.printStackTrace()
        }
        logger.info("ScriptProcessor took ${System.currentTimeMillis() - start} ms")
        return emptyList()
    }
}

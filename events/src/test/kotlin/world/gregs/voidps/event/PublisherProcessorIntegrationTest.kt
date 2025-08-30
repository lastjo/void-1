package world.gregs.voidps.event

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import com.tschuchort.compiletesting.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction

class PublisherProcessorIntegrationTest {

    abstract class Publishers {
        open suspend fun onEventSuspendInteract(id: String, name: String = "", approach: Boolean = false): Boolean = false
        open fun hasOnEventSuspendInteract(id: String, name: String = "", approach: Boolean = false): Boolean = false

        open suspend fun onEventSuspend(id: String, name: String): Boolean = false

        open suspend fun onEventSuspend(id: String): Boolean = false

        open fun hasOnEventSuspend(id: String, name: String): Boolean = false

        open fun hasOnEvent(id: String, name: String, approach: Boolean): Boolean = false

        open fun onEvent(id: String, name: String): Boolean = false

        open fun onEvent(id: String): Boolean = false
    }

    private fun compilation(
        source: String,
        required: List<String> = listOf("String"),
        notification: Boolean = false,
        suspend: Boolean = false,
        default: Any = false,
        interaction: Boolean = false,
    ) = KotlinCompilation().apply {
        sources = listOf(SourceFile.kotlin("MyHandler.kt", source))
        inheritClassPath = true
        kspWithCompilation = true
        symbolProcessorProviders = listOf(
            // provider for your processor
            TestProcessorProvider(required, notification, suspend, default, interaction),
        )
        messageOutputStream = System.out
    }

    private fun compilation(
        source: String,
        function: KFunction<*>,
        hasFunction: KFunction<*>? = null,
        notification: Boolean = false,
        default: Any = false,
    ) = KotlinCompilation().apply {
        sources = listOf(SourceFile.kotlin("MyHandler.kt", source))
        inheritClassPath = true
        kspWithCompilation = true
        symbolProcessorProviders = listOf(
            // provider for your processor
            TestMagicProcessorProvider(function, hasFunction, notification, default),
        )
        messageOutputStream = System.out
    }

    private class OnEventPublisher(
        notification: Boolean = false,
        suspend: Boolean = false,
        default: Any = false,
        interaction: Boolean = false,
        required: List<String>,
    ) : Publisher(
        name = "OnEventPublisher",
        parameters = listOf(
            "id" to STRING,
            "name" to STRING,
        ),
        returnsDefault = default,
        notification = notification,
        suspendable = suspend,
        methodName = if (suspend) "onEventSuspend" else "onEvent",
        interaction = interaction,
        required = required,
        checkMethodName = if (interaction) "hasOnEvent" else null
    ) {
        override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
            // Supports optional multiple annotation values
            val values = (method.annotationArgs["value"] as? List<String>)
                ?: listOfNotNull(method.annotationArgs["value"] as? String)
            val list = mutableListOf<Pair<String, Any>>()
            val approach = method.annotationArgs["appraoch"] as? Boolean
            if (approach != null) {
                list.add("approach" to approach)
            }
            return values.map { list + listOf("id" to it) }
        }
    }

    private class OnMagicEventPublisher(
        function: KFunction<*>, hasFunction: KFunction<*>? = null, notification: Boolean = false, returnsDefault: Any? = null
    ) : Publisher(function, hasFunction, notification, returnsDefault) {
        override fun comparisons(method: Subscriber): List<List<Pair<String, Any>>> {
            // Supports optional multiple annotation values
            val values = (method.annotationArgs["value"] as? List<String>)
                ?: listOfNotNull(method.annotationArgs["value"] as? String)
            return values.map { listOf("id" to it) }
        }
    }

    private class TestProcessorProvider(
        private val required: List<String> = listOf("String"),
        private val notification: Boolean = false,
        private val suspend: Boolean = false,
        private val default: Any = false,
        private val interaction: Boolean = false,
    ) : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            superclass = Publishers::class.asClassName(),
            schemas = mapOf(
                "test.OnEvent" to listOf(
                    OnEventPublisher(notification, suspend, default, interaction, required),
                ),
            ),
        )
    }

    private class TestMagicProcessorProvider(
        val function: KFunction<*>, val hasFunction: KFunction<*>? = null, val notification: Boolean = false, val returnsDefault: Any? = null
    ) : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            superclass = Publishers::class.asClassName(),
            schemas = mapOf(
                "test.OnEvent" to listOf(
                    OnMagicEventPublisher(function, hasFunction, notification, returnsDefault),
                ),
            ),
        )
    }

    @Test
    fun `PublisherProcessor generates Publishers file`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("123")
                fun handle(id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val compilation = compilation(source)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        // Find generated file
        val generatedDir = compilation.kspSourcesDir
        val publishersFile = generatedDir.resolve("kotlin/world/gregs/voidps/engine/script/PublishersImpl.kt")

        assertTrue(publishersFile.exists(), "PublishersImpl.kt should be generated")

        val content = publishersFile.readText()
        assertTrue(content.contains("class PublishersImpl"), "PublishersImpl class should exist")
        assertTrue(content.contains("MyHandler"), "Generated code should reference MyHandler")
    }

    @Test
    fun `Processor fails on invalid suspendable subscriber`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("123")
                suspend fun badHandle(id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val result = compilation(source).compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("cannot be suspendable"), "Error should mention suspendable")
    }

    @Test
    fun `Processor fails on return type mismatch`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("123")
                fun badReturn(id: String): Int {
                    return 0
                }
            }
        """.trimIndent()

        val result = compilation(source, default = "default").compile()
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("must return a String"), "Error should mention return type")
    }

    @Test
    fun `Processor allows suspendable subscriber`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("123")
                suspend fun suspendableHandle(id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val compilation = compilation(source, suspend = true)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("suspend fun publish("), "Publish method should also be suspendable")
    }

    @Test
    fun `Interaction produces has method`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String, val approach: Boolean = false)

            class MyHandler {
                @OnEvent("123")
                suspend fun handler(id: String): Int {
                    return 0
                }
            }
        """.trimIndent()

        val compilation = compilation(source, Publishers::onEventSuspendInteract, Publishers::hasOnEventSuspendInteract)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventSuspendInteractPublisher.kt").readText()
        assertTrue(content.contains("public fun has("), "Publish should have 'has' method")
    }

    @Test
    fun `Star ended id uses startsWith`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("123*")
                fun handle(id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val compilation = compilation(source, suspend = true)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("id.startsWith(\"123\""), "Publish method should use startsWith()")
    }

    @Test
    fun `Star started id uses endsWith`() {
        @Language("kotlin")
        val source = """
            package test

            annotation class OnEvent(val value: String)

            class MyHandler {
                @OnEvent("*123")
                fun handle(id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val compilation = compilation(source, suspend = true)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("id.endsWith(\"123\""), "Publish method should use endsWith()")
    }

    @Test
    fun `Arguments resolve correctly by name and type`() {
        @Language("kotlin")
        val source = """
            package test
            annotation class OnEvent(val value: String = "")

            class MyHandler(val injected: Int) {
                // parameters intentionally flipped around
                @OnEvent("123")
                fun handle(name: String, id: String): Boolean {
                    return true
                }
            }
        """.trimIndent()

        val compilation = compilation(source)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        // Order of args in generated code should still be (id, name) as schema declares
        assertTrue(content.contains("myHandler.handle(name, id)"), "Arguments should map by name/type, even if order differs")
    }

    @Test
    fun `Non-notification generates when statements`() {
        @Language("kotlin")
        val source = """
            package test
            annotation class OnEvent(val value: String = "")

            class MyHandler {
                @OnEvent("A")
                fun first(id: String, name: String) = "first"

                @OnEvent("B")
                fun second(id: String, name: String) = "second"
            }
        """.trimIndent()

        val compilation = compilation(source)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("when"), "Should generate when block")
        assertTrue(content.contains("id == \"A\" ->"), "Should match first comparison")
        assertTrue(content.contains("id == \"B\" ->"), "Should match second comparison")
    }

    @Test
    fun `Notification allows multiple handlers per comparison`() {
        @Language("kotlin")
        val source = """
            package test
            annotation class OnEvent(val value: String = "")

            class HandlerOne {
                @OnEvent("X")
                fun one(id: String, name: String) = true
            }
            class HandlerTwo {
                @OnEvent("X")
                fun two(id: String, name: String) = true
            }
        """.trimIndent()

        val compilation = compilation(source, required = listOf("String", "String"), notification = true)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("handled = handled || handlerOne.one"), "Multiple subscribers for same comparison allowed")
        assertTrue(content.contains("|| handlerTwo.two"), "Second subscriber should also be included")
    }

    @Test
    fun `Notification types must return booleans`() {
        assertThrows(AssertionError::class.java) {
            object : Publisher(
                name = "Invalid",
                parameters = listOf("id" to STRING),
                returnsDefault = "notBoolean",
                notification = true,
                methodName = "",
                required = emptyList()
            ) {
                override fun comparisons(method: Subscriber) = emptyList<List<Pair<String, Any>>>()
            }
        }
    }

    @Test
    fun `Subscribers with no annotation values fall into else branch`() {
        @Language("kotlin")
        val source = """
            package test
            annotation class OnEvent

            class MyHandler {
                @OnEvent
                fun catchAll(id: String, name: String): Boolean = true
            }
        """.trimIndent()

        val compilation = compilation(source, required = listOf("String"))
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val content = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/OnEventPublisher.kt").readText()
        assertTrue(content.contains("else -> return myHandler.catchAll"), "Subscribers without annotation values should be in else branch")
    }

    @Test
    fun `Unknown constructor params are injected through Publishers`() {
        @Language("kotlin")
        val source = """
            package test
            annotation class OnEvent(val value: String = "")

            class NeedsDependency(val dep: Int) {
                @OnEvent("123")
                fun run(id: String, name: String): Boolean = true
            }
        """.trimIndent()

        val compilation = compilation(source)
        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val publishersFile = compilation.kspSourcesDir.resolve("kotlin/world/gregs/voidps/engine/script/PublishersImpl.kt")
        val content = publishersFile.readText()
        assertTrue(content.contains("dep: Int"), "PublishersImpl constructor should inject dep")
        assertTrue(content.contains("NeedsDependency(dep)"), "Publisher should construct NeedsDependency with dep injected")
    }
}

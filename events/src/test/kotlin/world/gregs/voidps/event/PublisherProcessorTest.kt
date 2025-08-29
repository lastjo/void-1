package world.gregs.voidps.event

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.ClassName
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PublisherProcessorTest {

    private val codeGenerator: CodeGenerator = mockk(relaxed = true)
    private val logger: KSPLogger = mockk(relaxed = true)

    private val dummyPublisher = object : Publisher(
        name = "TestPublisher",
        suspendable = false,
        parameters = listOf("id" to ClassName("kotlin", "String")),
        returnsDefault = true,
        notification = true
    ) {
        override fun comparisons(
            method: Subscriber
        ): List<List<Pair<String, Any>>> {
            return listOf(listOf("id" to "123"))
        }
    }

    @Test
    fun `Find schema matches expected parameters`() {
        val schemas = mapOf(
            "MyAnnotation" to listOf(
                listOf("String") to dummyPublisher
            )
        )

        val processor = PublisherProcessor(codeGenerator, logger, schemas)
        val found = processor.findSchema("MyAnnotation", listOf("id" to "String"))

        assertEquals(dummyPublisher, found)
    }

    @Test
    fun `Find schema throws when schema not found`() {
        val processor = PublisherProcessor(codeGenerator, logger, emptyMap())
        assertThrows(IllegalStateException::class.java) {
            processor.findSchema("Missing", listOf("id" to "String"))
        }
    }
}

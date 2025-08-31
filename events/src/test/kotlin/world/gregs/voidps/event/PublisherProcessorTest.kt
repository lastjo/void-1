package world.gregs.voidps.event

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import world.gregs.voidps.event.PublisherProcessorIntegrationTest.Publishers

class PublisherProcessorTest {

    private val codeGenerator: CodeGenerator = mockk(relaxed = true)
    private val logger: KSPLogger = mockk(relaxed = true)

    private val dummyPublisher = object : Publisher(
        name = "TestPublisher",
        suspendable = false,
        parameters = listOf("id" to STRING),
        returnsDefault = true,
        notification = true,
        methodName = "",
        required = listOf(STRING)
    ) {
        override fun comparisons(
            method: Subscriber,
        ): List<List<Comparator>> = listOf(listOf(Equals("id", "123")))
    }

    @Test
    fun `Find schema matches expected parameters`() {
        val schemas = mapOf(
            "MyAnnotation" to listOf(
                dummyPublisher,
            ),
        )

        val processor = PublisherProcessor(codeGenerator, logger, schemas, Publishers::class.asClassName())
        val found = processor.findSchema("MyAnnotation", listOf(Pair("id", STRING)))

        assertEquals(dummyPublisher, found)
    }

    @Test
    fun `Find schema throws when schema not found`() {
        val processor = PublisherProcessor(codeGenerator, logger, emptyMap(), Publishers::class.asClassName())
        assertThrows(IllegalStateException::class.java) {
            processor.findSchema("Missing", listOf(Pair("id", STRING)))
        }
    }
}

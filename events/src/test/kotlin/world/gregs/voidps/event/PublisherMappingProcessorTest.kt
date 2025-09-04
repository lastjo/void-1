package world.gregs.voidps.event

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import world.gregs.voidps.event.PublisherMappingProcessorIntegrationTest.Publishers

class PublisherMappingProcessorTest {

    private val codeGenerator: CodeGenerator = mockk(relaxed = true)
    private val logger: KSPLogger = mockk(relaxed = true)

    private val dummyMapping = object : PublisherMapping(
        name = "TestPublisher",
        suspendable = false,
        parameters = listOf("id" to STRING),
        returnsDefault = true,
        notification = true,
        methodName = "",
        required = listOf(STRING),
    ) {
        override fun conditions(
            method: Subscriber,
        ): List<List<Condition>> = listOf(listOf(Equals("id", "123")))
    }

    @Test
    fun `Find schema matches expected parameters`() {
        val schemas = mapOf(
            "MyAnnotation" to listOf(
                dummyMapping,
            ),
        )

        val processor = PublisherProcessor(codeGenerator, logger, schemas, Publishers::class.asClassName())
        val found = processor.findSchema("MyAnnotation", listOf(Pair("id", STRING)))

        assertEquals(dummyMapping, found)
    }

    @Test
    fun `Find schema returns null when schema not found`() {
        val processor = PublisherProcessor(codeGenerator, logger, emptyMap(), Publishers::class.asClassName())
        assertNull(processor.findSchema("Missing", listOf(Pair("id", STRING))))
    }
}

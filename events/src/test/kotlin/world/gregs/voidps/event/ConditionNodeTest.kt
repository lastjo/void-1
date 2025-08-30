package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConditionNodeTest {
    private val dummyPublisher = object : Publisher(
        name = "TestPublisher",
        suspendable = false,
        parameters = listOf("id" to ClassName("kotlin", "String")),
        returnsDefault = true,
        notification = true,
        methodName = "",
        required = listOf()
    ) {
        override fun comparisons(
            method: Subscriber,
        ): List<List<Pair<String, Any>>> = listOf(listOf("id" to "123"))
    }

    @Test
    fun `Build a condition tree inserts subscribers correctly`() {
        val subscriber = Subscriber(
            className = ClassName("test", "MyHandler"),
            methodName = "onEvent",
            parameters = listOf("id" to "String"),
            schema = dummyPublisher,
            annotationArgs = emptyMap(),
            classParams = emptyList(),
            returnType = "kotlin.Unit",
        )

        val root = ConditionNode.buildTree(dummyPublisher, listOf(subscriber))

        assertEquals(1, root.children.size)
        assertEquals("id" to "123", root.children.first().condition)
        assertEquals(subscriber, root.children.first().subscribers.first())
    }

    @Test
    fun `Arguments maps by name first`() {
        val subscriber = Subscriber(
            className = ClassName("test", "MyHandler"),
            methodName = "onEvent",
            parameters = listOf("id" to "String"),
            schema = dummyPublisher,
            annotationArgs = emptyMap(),
            classParams = emptyList(),
            returnType = "kotlin.Unit",
        )

        val args = ConditionNode.arguments(subscriber, dummyPublisher)
        assertEquals(listOf("id"), args)
    }
}

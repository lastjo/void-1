package world.gregs.voidps.event

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STRING
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ComparatorNodeTest {
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
        ): List<List<Comparator>> = listOf(listOf(Equals("id", "123")))
    }

    @Test
    fun `Build a condition tree inserts subscribers correctly`() {
        val subscriber = Subscriber(
            className = ClassName("test", "MyHandler"),
            methodName = "onEvent",
            parameters = listOf("id" to STRING),
            schema = dummyPublisher,
            annotationArgs = emptyMap(),
            classParams = emptyList(),
            returnType = "kotlin.Unit",
        )

        val root = ConditionNode.buildTree(dummyPublisher, listOf(subscriber))

        assertEquals(1, root.children.size)
        assertEquals(Equals("id", "123"), root.children.first().comparator)
        assertEquals(subscriber, root.children.first().subscribers.first())
    }

    @Test
    fun `Arguments maps by name first`() {
        val subscriber = Subscriber(
            className = ClassName("test", "MyHandler"),
            methodName = "onEvent",
            parameters = listOf("id" to STRING),
            schema = dummyPublisher,
            annotationArgs = emptyMap(),
            classParams = emptyList(),
            returnType = "kotlin.Unit",
        )

        val args = dummyPublisher.arguments(subscriber)
        assertEquals(listOf("id"), args)
    }
}

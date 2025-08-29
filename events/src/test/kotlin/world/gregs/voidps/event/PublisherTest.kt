package world.gregs.voidps.event

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PublisherTest {
    @Test
    fun `Publisher requires boolean default when notification is true`() {
        assertThrows(AssertionError::class.java) {
            object : Publisher(
                name = "Invalid",
                notification = true,
                parameters = emptyList(),
                returnsDefault = "NotBoolean",
                overrideMethod = "",
            ) {
                override fun comparisons(
                    method: Subscriber,
                ): List<List<Pair<String, Any>>> = emptyList()
            }
        }
    }
}

package world.gregs.voidps.engine.timer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.GameLoop
import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.event.Publishers

internal class TimerQueueTest : TimersTest() {

    @BeforeEach
    override fun setup() {
        super.setup()
        val queue = TimerQueue(World)
        timers = queue
    }

    @Test
    fun `Multiple timers run at once`() {
        timers.start("1")
        timers.start("2")
        repeat(3) {
            timers.run()
            GameLoop.tick++
        }
        assertTrue(timers.contains("1"))
        assertTrue(timers.contains("2"))
        assertEquals(Pair("start_1", false), emitted.pop())
        assertEquals(Pair("start_2", false), emitted.pop())
        repeat(3) {
            assertEquals(Pair("tick_1", false), emitted.pop())
            assertEquals(Pair("tick_2", false), emitted.pop())
        }
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Updating next timer tick changes order`() {
        Publishers.set(object : Publishers {
            override fun timerStart(source: Entity, timer: String, restart: Boolean) = 2
        })
        timers.start("mutable")
        Publishers.set(object : Publishers {
            override fun timerStart(source: Entity, timer: String, restart: Boolean) = 3
        })
        timers.start("fixed")

        repeat(3) {
            timers.run()
            GameLoop.tick++
        }
        assertFalse(timers.contains("timer"))
        assertEquals(Pair("start_mutable", false), emitted.pop())
        assertEquals(Pair("start_fixed", false), emitted.pop())
        assertEquals(Pair("tick_mutable", false), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Can't run two timers with the same name`() {
        assertTrue(timers.start("1"))
        assertFalse(timers.start("1"))
    }
}

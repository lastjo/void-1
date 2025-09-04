package world.gregs.voidps.engine.timer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.GameLoop
import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.TimerState
import java.util.*

abstract class TimersTest {

    lateinit var emitted: LinkedList<Pair<String, Boolean>>
    lateinit var timers: Timers

    open fun setup() {
        GameLoop.tick = 0
        emitted = LinkedList()
        Publishers.clear()
        set()
    }

    fun set(start: Int = -1, tick: Int = -1) {
        Publishers.set(object : Publishers {
            override fun timerStart(source: Entity, timer: String, restart: Boolean): Int {
                emitted.add("start_$timer" to restart)
                return start
            }
            override fun timerTick(source: Entity, timer: String): Int {
                emitted.add("tick_$timer" to false)
                return tick
            }
            override fun timerStop(source: Entity, timer: String, logout: Boolean): Boolean {
                emitted.add("stop_$timer" to logout)
                return super.timerStop(source, timer, logout)
            }
        })
    }

    @Test
    fun `Restart a timer`() {
        set(2)
        timers.restart("timer")
        assertTrue(timers.contains("timer"))
        assertEquals(Pair("start_timer", true), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Cancelled start event doesn't add timer`() {
        set(TimerState.CANCEL)
        assertFalse(timers.start("timer"))
        assertFalse(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Timers emit at a constant interval`() {
        set(2)
        assertTrue(timers.start("timer"))
        repeat(5) {
            timers.run()
            GameLoop.tick++
        }
        assertTrue(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        repeat(2) {
            assertEquals(Pair("tick_timer", false), emitted.pop())
        }
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Timer can temp modify interval`() {
        set(2, 1)
        assertTrue(timers.start("timer"))
        repeat(4) {
            timers.run()
            GameLoop.tick++
        }
        assertTrue(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        repeat(2) {
            assertEquals(Pair("tick_timer", false), emitted.pop())
        }
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Timers with 0 delay repeats every tick`() {
        set(0, 1)
        timers.start("timer")
        repeat(3) {
            timers.run()
            GameLoop.tick++
        }
        assertTrue(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        repeat(3) {
            assertEquals(Pair("tick_timer", false), emitted.pop())
        }
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Timers with cancelled tick events are removed`() {
        set(0, TimerState.CANCEL)
        timers.start("timer")
        repeat(3) {
            timers.run()
            GameLoop.tick++
        }
        assertFalse(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        assertEquals(Pair("tick_timer", false), emitted.pop())
        assertEquals(Pair("stop_timer", false), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Clearing a timer cancels it`() {
        set(0)
        timers.start("timer")
        timers.stop("timer")
        assertEquals(Pair("start_timer", false), emitted.pop())
        assertEquals(Pair("stop_timer", false), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Stopping timers emit stop`() {
        timers.start("timer")
        timers.stopAll()
        assertFalse(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        assertEquals(Pair("stop_timer", true), emitted.pop())
        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `Cleared timers are cancelled`() {
        timers.start("timer")
        timers.clearAll()
        assertFalse(timers.contains("timer"))
        assertEquals(Pair("start_timer", false), emitted.pop())
        assertTrue(emitted.isEmpty())
        assertTrue(emitted.isEmpty())
    }
}

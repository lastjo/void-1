package world.gregs.voidps.engine.timer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.voidps.engine.GameLoop
import world.gregs.voidps.engine.entity.character.npc.NPC

internal class TimerSlotTest : TimersTest() {

    @BeforeEach
    override fun setup() {
        super.setup()
        timers = TimerSlot(NPC())
    }

    @Test
    fun `Overriding cancels previous timer`() {
        timers.start("1")
        timers.start("2")
        repeat(3) {
            timers.run()
            GameLoop.tick++
        }
        assertFalse(timers.contains("1"))
        assertTrue(timers.contains("2"))
        assertEquals(Pair("start_1", false), emitted.pop())
        assertEquals(Pair("start_2", false), emitted.pop())
        assertEquals(Pair("stop_1", false), emitted.pop())
        assertEquals(Pair("tick_2", false), emitted.pop())
        assertEquals(Pair("tick_2", false), emitted.pop())
        assertEquals(Pair("tick_2", false), emitted.pop())
        assertTrue(emitted.isEmpty())
    }
}

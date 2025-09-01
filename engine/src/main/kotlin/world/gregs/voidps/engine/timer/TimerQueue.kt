package world.gregs.voidps.engine.timer

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.EventDispatcher
import world.gregs.voidps.engine.event.Publishers
import java.util.*

class TimerQueue(
    private val events: EventDispatcher,
    private val entity: Entity,
) : Timers {

    val queue = PriorityQueue<Timer>()
    val names = mutableSetOf<String>()
    private val changes = mutableListOf<Timer>()

    override fun start(name: String, restart: Boolean): Boolean {
        if (names.contains(name)) {
            return false
        }
        val interval = Publishers.all.timerStart(entity, name, restart)
        val start = TimerStart(name, restart)
        events.emit(start)
        if (start.cancelled) {
            return false
        }
        val timer = Timer(name, if (interval != -1) interval else start.interval)
        queue.add(timer)
        names.add(name)
        return true
    }

    override fun contains(name: String): Boolean = names.contains(name)

    override fun run() {
        val iterator = queue.iterator()
        var timer: Timer
        while (iterator.hasNext()) {
            timer = iterator.next()
            if (!timer.ready()) {
                break
            }
            iterator.remove()
            timer.reset()
            val next = Publishers.all.timerTick(entity, timer.name)
            val tick = TimerTick(timer.name)
            events.emit(tick)
            if (tick.cancelled || next == 0) {
                names.remove(timer.name)
                events.emit(TimerStop(timer.name, logout = false))
            } else {
                if (tick.nextInterval != -1) {
                    timer.next(tick.nextInterval)
                }
                changes.add(timer)
            }
        }
        if (changes.isNotEmpty()) {
            queue.addAll(changes)
            changes.clear()
        }
    }

    override fun stop(name: String) {
        if (clear(name)) {
            Publishers.all.timerStop(entity, name, logout = false)
            events.emit(TimerStop(name, logout = false))
        }
    }

    override fun clear(name: String): Boolean = names.remove(name) && queue.removeIf { it.name == name }

    override fun clearAll() {
        names.clear()
        queue.clear()
    }

    override fun stopAll() {
        val names = names.toList()
        clearAll()
        for (name in names) {
            Publishers.all.timerStop(entity, name, logout = true)
            events.emit(TimerStop(name, logout = true))
        }
    }
}

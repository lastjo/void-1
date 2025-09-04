package world.gregs.voidps.engine.timer

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.TimerState
import java.util.*

class TimerQueue(
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
        if (interval == TimerState.CANCEL) {
            return false
        }
        val timer = Timer(name, interval)
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
            val nextInterval = Publishers.all.timerTick(entity, timer.name)
            if (nextInterval == TimerState.CANCEL) {
                names.remove(timer.name)
                Publishers.all.timerStop(entity, timer.name, logout = true)
            } else {
                if (nextInterval != TimerState.CONTINUE) {
                    timer.next(nextInterval)
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
        }
    }
}

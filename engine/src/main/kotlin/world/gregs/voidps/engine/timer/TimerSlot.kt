package world.gregs.voidps.engine.timer

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.EventDispatcher
import world.gregs.voidps.engine.event.Publishers

class TimerSlot(
    private val events: EventDispatcher,
    private val entity: Entity,
) : Timers {

    private var timer: Timer? = null

    override fun start(name: String, restart: Boolean): Boolean {
        val start = TimerStart(name, restart)
        val interval = Publishers.all.timerStart(entity, name, restart)
        events.emit(start)
        if (start.cancelled) {
            return false
        }
        if (timer != null) {
            Publishers.all.timerStop(entity, timer!!.name, logout = false)
            events.emit(TimerStop(timer!!.name, logout = false))
        }
        this.timer = Timer(name, if (interval != -1) interval else start.interval)
        return true
    }

    override fun contains(name: String): Boolean = timer?.name == name

    override fun run() {
        val timer = timer ?: return
        if (!timer.ready()) {
            return
        }
        timer.reset()
        val tick = TimerTick(timer.name)
        val next = Publishers.all.timerTick(entity, timer.name)
        events.emit(tick)
        if (tick.cancelled) {
            Publishers.all.timerStop(entity, timer.name, logout = false)
            events.emit(TimerStop(timer.name, logout = false))
            this.timer = null
        } else if (tick.nextInterval != -1) {
            timer.next(if (next != -1) next else tick.nextInterval)
        }
    }

    override fun stop(name: String) {
        if (contains(name)) {
            Publishers.all.timerStop(entity, timer!!.name, logout = false)
            events.emit(TimerStop(timer!!.name, logout = false))
            timer = null
        }
    }

    override fun clear(name: String): Boolean {
        if (contains(name)) {
            timer = null
            return true
        }
        return false
    }

    override fun clearAll() {
        timer = null
    }

    override fun stopAll() {
        if (timer != null) {
            Publishers.all.timerStop(entity, timer!!.name, logout = true)
            events.emit(TimerStop(timer!!.name, logout = true))
        }
        timer = null
    }
}

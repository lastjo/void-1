package world.gregs.voidps.engine.timer

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.EventDispatcher
import world.gregs.voidps.engine.event.Publishers

class TimerSlot(
    private val events: EventDispatcher,
    private val entity: Entity,
    private val publishers: Publishers,
) : Timers {

    private var timer: Timer? = null

    override fun start(name: String, restart: Boolean): Boolean {
        val start = TimerStart(name, restart)
        publishers.timerStart(entity, name, restart)
        events.emit(start)
        if (start.cancelled) {
            return false
        }
        if (timer != null) {
            publishers.timerStop(entity, timer!!.name, logout = false)
            events.emit(TimerStop(timer!!.name, logout = false))
        }
        this.timer = Timer(name, start.interval)
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
        publishers.timerTick(entity, timer.name)
        events.emit(tick)
        if (tick.cancelled) {
            publishers.timerStop(entity, timer.name, logout = false)
            events.emit(TimerStop(timer.name, logout = false))
            this.timer = null
        } else if (tick.nextInterval != -1) {
            timer.next(tick.nextInterval)
        }
    }

    override fun stop(name: String) {
        if (contains(name)) {
            publishers.timerStop(entity, timer!!.name, logout = false)
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
            publishers.timerStop(entity, timer!!.name, logout = true)
            events.emit(TimerStop(timer!!.name, logout = true))
        }
        timer = null
    }
}

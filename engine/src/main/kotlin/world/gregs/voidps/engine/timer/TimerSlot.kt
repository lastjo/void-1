package world.gregs.voidps.engine.timer

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.type.TimerState

class TimerSlot(
    private val entity: Entity,
) : Timers {

    private var timer: Timer? = null

    override fun start(name: String, restart: Boolean): Boolean {
        val interval = Publishers.all.timerStart(entity, name, restart)
        if (interval == TimerState.CANCEL) {
            return false
        }
        if (timer != null) {
            Publishers.all.timerStop(entity, timer!!.name, logout = false)
        }
        this.timer = Timer(name, interval)
        return true
    }

    override fun contains(name: String): Boolean = timer?.name == name

    override fun run() {
        val timer = timer ?: return
        if (!timer.ready()) {
            return
        }
        timer.reset()
        val nextInterval = Publishers.all.timerTick(entity, timer.name)
        if (nextInterval == TimerState.CANCEL) {
            Publishers.all.timerStop(entity, timer.name, logout = false)
            this.timer = null
        } else if (nextInterval != TimerState.CONTINUE) {
            timer.next(nextInterval)
        }
    }

    override fun stop(name: String) {
        if (contains(name)) {
            Publishers.all.timerStop(entity, timer!!.name, logout = false)
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
        }
        timer = null
    }
}

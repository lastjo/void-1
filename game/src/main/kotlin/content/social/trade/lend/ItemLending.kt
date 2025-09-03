package content.social.trade.lend

import content.social.trade.lend.Loan.returnLoan
import content.social.trade.returnedItems
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.variable.remaining
import world.gregs.voidps.engine.client.variable.stop
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.playerDespawn
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.timer.*
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.Despawn
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerStop
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class ItemLending(private val players: Players) {

    @Spawn
    fun spawn(player: Player) {
        checkBorrowComplete(player)
        checkLoanComplete(player)
    }

    @Despawn
    fun despawn(player: Player) {
        checkBorrowUntilLogout(player)
        checkLoanUntilLogout(player)
    }

    @TimerStart("loan_message")
    fun start(player: Player): Int {
        val remaining = player.remaining("lend_timeout", epochSeconds())
        return TimeUnit.SECONDS.toTicks(remaining)
    }

    @TimerStop("loan_message")
    fun stop(player: Player, logout: Boolean) {
        if (!logout) {
            stopLending(player)
        }
    }

    @TimerStart("borrow_message")
    fun startBorrow(player: Player): Int = TimeUnit.MINUTES.toTicks(1)

    @TimerTick("borrow_message")
    fun tick(player: Player): Int {
        val remaining = player.remaining("borrow_timeout", epochSeconds())
        if (remaining <= 0) {
            player.message("Your loan has expired; the item you borrowed will now be returned to its owner.")
            return TimerState.CANCEL
        } else if (remaining == 60) {
            player.message("The item you borrowed will be returned to its owner in a minute.")
        }
        return TimerState.CONTINUE
    }

    @TimerStop("borrow_message")
    fun stopBorrow(player: Player, logout: Boolean) {
        if (!logout) {
            returnLoan(player)
        }
    }

    /**
     * Reschedule timers on player login
     * On logout return items borrowed or lent until logout
     */

    fun checkBorrowComplete(player: Player) {
        if (!player.contains("borrowed_item")) {
            return
        }
        val remaining = player.remaining("borrow_timeout", epochSeconds())
        if (remaining <= 0) {
            player.message("The item you borrowed has been returned to its owner.")
            returnLoan(player)
        } else {
            player.softTimers.start("borrow_message", true)
        }
    }

    fun checkLoanComplete(player: Player) {
        if (!player.returnedItems.isFull()) {
            return
        }
        val remaining = player.remaining("lend_timeout", epochSeconds())
        if (remaining <= 0) {
            stopLending(player)
        } else {
            player.softTimers.start("loan_message", true)
        }
    }

    fun checkBorrowUntilLogout(player: Player) {
        if (!player.contains("borrow_timeout") && player.contains("borrowed_item")) {
            returnLoan(player)
        }
    }

    fun checkLoanUntilLogout(player: Player) {
        if (!player.contains("lend_timeout") && player.returnedItems.isFull() && player.contains("lent_to")) {
            val name: String? = player["lent_to"]
            player.stop("lend_timeout")
            player.softTimers.stop("loan_message")
            val borrower = players.get(name ?: return) ?: return
            borrower.stop("borrow_timeout")
            borrower.softTimers.stop("borrow_message")
            borrower.message("The item you borrowed has been returned to its owner.")
        }
    }

    fun stopLending(player: Player) {
        player.message("The item you lent has been returned to your collection box.")
        player.clear("lent_to")
        player.clear("lent_item_id")
        player.clear("lent_item_amount")
    }
}

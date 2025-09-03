package content.bot

import com.github.michaelbull.logging.InlineLogger
import content.bot.interact.navigation.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import world.gregs.voidps.engine.Contexts
import world.gregs.voidps.engine.entity.AiTick
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.event.onEvent
import world.gregs.voidps.type.sub.Subscribe

class DecisionMaking(
    private val players: Players,
    private val tasks: TaskManager,
) {

    val scope = CoroutineScope(Contexts.Game)
    val logger = InlineLogger("Bot")

    @Subscribe("start_bot")
    fun handle(player: Player) {
        if (!player.contains("task_bot") || player.contains("task_started")) {
            return
        }
        val name: String = player["task_bot"]!!
        val task = tasks.get(name)
        if (task == null) {
            player.clear("task_bot")
        } else {
            assign(player, task)
        }
    }

    @Subscribe("ai_tick")
    fun tick(world: World) {
        players.forEach { player ->
            if (player.isBot) {
                val bot: Bot = player["bot"]!!
                if (!bot.contains("task_bot")) {
                    val lastTask: String? = bot["last_task_bot"]
                    assign(player, tasks.assign(bot, lastTask))
                }
                player.bot.resume("tick")
            }
        }
    }

    fun assign(bot: Player, task: Task) {
        if (bot["debug", false]) {
            logger.debug { "Task assigned: ${bot.accountName} - ${task.name}" }
        }
        val last = bot.get<String>("task_bot")
        if (last != null) {
            bot["last_task_bot"] = last
        }
        bot["task_bot"] = task.name
        bot["task_started"] = true
        task.spaces--
        scope.launch {
            try {
                task.block.invoke(bot)
            } catch (t: Throwable) {
                logger.warn(t) { "Task cancelled for $bot" }
            }
            bot.clear("task_bot")
            task.spaces++
        }
    }
}

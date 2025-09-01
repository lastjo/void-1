package content.entity.player.modal.tab

import com.github.michaelbull.logging.InlineLogger
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.client.clearCamera
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.variable.variableSet
import world.gregs.voidps.engine.data.definition.QuestDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.timer.timerStart
import world.gregs.voidps.engine.timer.timerStop
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.*

class QuestJournals(private val questDefinitions: QuestDefinitions) {

    val logger = InlineLogger()

    @Open("quest_journals")
    fun open(player: Player, id: String) {
        player.interfaceOptions.unlock(id, "journals", 0 until 201, "View")
        player.sendVariable("quest_points")
        player.sendVariable("quest_points_total") // set total quest points available in variables-player.yml
        player.sendVariable("unstable_foundations")
        for (quest in questDefinitions.ids.keys) {
            player.sendVariable(quest)
        }
    }

    @Interface(component = "journals", id = "quest_journals")
    fun journals(player: Player, itemSlot: Int) {
        val quest = questDefinitions.getOrNull(itemSlot)
        if (quest == null) {
            logger.warn { "Unknown quest $itemSlot" }
            return
        }
        Publishers.all.publishPlayer(player, "quest_journal", quest.stringId)
        player.emit(OpenQuestJournal(player, quest.stringId))
    }

    @Variable
    fun questUpdated(player: Player, key: String) {
        if (questDefinitions.ids.containsKey(key)) {
            player.softTimers.start("refresh_quest_journal")
        }
    }

    @TimerStart("refresh_quest_journal")
    fun start(player: Player): Int {
        return 1
    }

    @TimerStop("refresh_quest_journal")
    fun stop(player: Player) {
        player.refreshQuestJournal()
    }

    @Spawn
    fun spawn(player: Player) {
        player.clearCamera()
    }

}

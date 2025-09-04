package content.entity.player.dialogue.type

import content.entity.sound.jingle
import world.gregs.voidps.engine.client.ui.*
import world.gregs.voidps.engine.client.ui.chat.an
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.Skill.*
import world.gregs.voidps.engine.entity.character.player.skill.exp.Experience
import world.gregs.voidps.engine.entity.character.player.skill.level.MaxLevelChanged
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.suspend.ContinueSuspension
import world.gregs.voidps.engine.suspend.SuspendableContext
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.LevelChange

private const val LEVEL_UP_INTERFACE_ID = "dialogue_level_up"

suspend fun SuspendableContext<Player>.levelUp(skill: Skill, text: String) {
    levelUp(player, skill, text)
    ContinueSuspension.get(player)
    player.close(LEVEL_UP_INTERFACE_ID)
}

fun levelUp(player: Player, skill: Skill, text: String) {
    val lines = text.trimIndent().lines()
    check(player.open(LEVEL_UP_INTERFACE_ID)) { "Unable to open level up interface for $player" }
    for ((index, line) in lines.withIndex()) {
        player.interfaces.sendText(LEVEL_UP_INTERFACE_ID, "line${index + 1}", line)
    }
    player["level_up_icon"] = skill.name
}

class LevelUp {

    @world.gregs.voidps.type.sub.Experience
    fun exp(player: Player, skill: Skill, from: Double, to: Double) {
        val previousLevel = Experience.level(skill, from)
        val currentLevel = Experience.level(skill, to)
        if (currentLevel != previousLevel) {
            player.levels.restore(skill, currentLevel - previousLevel)
            Publishers.all.levelChangePlayer(player, skill, previousLevel, currentLevel, max = true)
            Publishers.all.levelChangeCharacter(player, skill, previousLevel, currentLevel, max = true)
            player.emit(MaxLevelChanged(skill, previousLevel, currentLevel))
        }
    }

    @LevelChange(max = true)
    fun level(player: Player, skill: Skill, from: Int, to: Int) {
        if (from >= to) {
            return
        }
        if (player["skip_level_up", false]) {
            return
        }
        val unlock = when (skill) {
            Agility -> false
            Construction -> to.rem(10) == 0
            Constitution, Strength -> to >= 50
            Hunter -> to.rem(2) == 0
            else -> true // TODO has unlocked something
        }
        player.jingle("level_up_${skill.name.lowercase()}${if (unlock) "_unlock" else ""}", 0.5)
        player.addVarbit("skill_stat_flash", skill.name.lowercase())
        val level = if (skill == Constitution) to / 10 else to
        levelUp(
            player,
            skill,
            """
                Congratulations! You've just advanced${skill.name.an()} ${skill.name} level!
                You have now reached level $level!
            """,
        )
    }

    @Combat(stage = CombatStage.DAMAGE)
    fun combat(player: Player, source: Character) {
        if (!(player.menu ?: player.dialogue).isNullOrBlank()) {
            player.closeInterfaces()
        }
    }
}

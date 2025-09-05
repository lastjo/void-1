package content.entity.player.dialogue.type

import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.Experience
import world.gregs.voidps.engine.entity.character.player.skill.level.PlayerLevels
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.suspend.ContinueSuspension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LevelUpTest : DialogueTest() {

    @Test
    fun `Send level up`() {
        var resumed = false
        dialogue {
            levelUp(Skill.Runecrafting, "Congrats\nLevel")
            resumed = true
        }
        (player.dialogueSuspension as ContinueSuspension).resume(Unit)
        verify {
            player.open("dialogue_level_up")
            interfaces.sendText("dialogue_level_up", "line1", "Congrats")
            interfaces.sendText("dialogue_level_up", "line2", "Level")
            player["level_up_icon"] = Skill.Runecrafting.name
        }
        assertTrue(resumed)
    }

    @Test
    fun `Level up not sent if interface not opened`() {
        every { player.open("dialogue_level_up") } returns false
        assertThrows<IllegalStateException> {
            dialogueBlocking {
                levelUp(Skill.Agility, "One\nTwo")
            }
        }

        coVerify(exactly = 0) {
            interfaces.sendText("dialogue_level_up", "line1", "One")
        }
    }

    @Test
    fun `Listen to level up`() {
        var calls = 0
        val player = Player()
        player.levels.link(player, PlayerLevels(Experience()))
        val publishers = object : Publishers {
            override fun levelChangeCharacter(character: Character, skill: Skill, from: Int, to: Int, max: Boolean): Boolean {
                calls++
                assertEquals(player, character)
                assertEquals(Skill.Magic, skill)
                assertEquals(1, from)
                assertEquals(10, to)
                assertEquals(true, max)
                return super.levelChangeCharacter(character, skill, from, to, max)
            }
        }
        Publishers.set(publishers)
        LevelUp().exp(player, Skill.Magic, 0.0, 1154.0)
        assertEquals(1, calls)
    }
}

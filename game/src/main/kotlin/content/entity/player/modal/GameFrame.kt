package content.entity.player.modal

import net.pearx.kasechange.toSnakeCase
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.instruction.instruction
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.client.ui.event.interfaceRefresh
import world.gregs.voidps.engine.client.ui.hasOpen
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.queue.weakQueue
import world.gregs.voidps.network.client.instruction.ChangeDisplayMode
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Refresh

class GameFrame {

    val list = listOf(
        "chat_box",
        "chat_background",
        "filter_buttons",
        "private_chat",
        "health_orb",
        "prayer_orb",
        "energy_orb",
        "summoning_orb",
        "combat_styles",
        "task_system",
        "task_popup",
        "stats",
        "quest_journals",
        "inventory",
        "worn_equipment",
        "prayer_list",
        "modern_spellbook",
        "friends_list",
        "ignore_list",
        "clan_chat",
        "options",
        "emotes",
        "music_player",
        "notes",
        "area_status_icon",
    )


    init {
        instruction<ChangeDisplayMode> { player ->
            if (player.interfaces.displayMode == displayMode || !player.hasOpen("graphics_options")) {
                return@instruction
            }
            player.interfaces.setDisplayMode(displayMode)
        }
    }

    @Interface("Combat Styles", "combat_styles", "toplevel*")
    fun combatStyles(player: Player) {
        player["tab", false] = "CombatStyles"
    }

    @Interface("Task System", "task_system", "toplevel*")
    fun taskSystem(player: Player) {
        player["tab", false] = Tab.TaskSystem.name
    }

    @Interface("Stats", "stats", "toplevel*")
    fun stats(player: Player) {
        player["tab", false] = Tab.Stats.name
    }

    @Interface("Quest Journals", "quest_journals", "toplevel*")
    fun questJournals(player: Player) {
        player["tab", false] = Tab.QuestJournals.name
    }

    @Interface("Inventory", "inventory", "toplevel*")
    fun inventory(player: Player) {
        player["tab", false] = Tab.Inventory.name
    }

    @Interface("Worn Equipment", "worn_equipment", "toplevel*")
    fun wornEquipment(player: Player) {
        player["tab", false] = Tab.WornEquipment.name
    }

    @Interface("Prayer List", "prayer_list", "toplevel*")
    fun prayerList(player: Player) {
        player["tab", false] = Tab.PrayerList.name
    }

    @Interface("Magic Spellbook", "magic_spellbook", "toplevel*")
    fun magicSpellbook(player: Player) {
        player["tab", false] = Tab.MagicSpellbook.name
    }

    @Interface("Friends List", "friends_list", "toplevel*")
    fun friendsList(player: Player) {
        player["tab", false] = Tab.FriendsList.name
    }

    @Interface("Ignore List", "ignore_list", "toplevel*")
    fun ignoreList(player: Player) {
        player["tab", false] = Tab.IgnoreList.name
    }

    @Interface("Options", "options", "toplevel*")
    fun options(player: Player) {
        player["tab", false] = Tab.Options.name
    }

    @Interface("Clan Chat", "clan_chat", "toplevel*")
    fun clanChat(player: Player) {
        player["tab", false] = Tab.ClanChat.name
    }

    @Interface("Emotes", "emotes", "toplevel*")
    fun emotes(player: Player) {
        player["tab", false] = Tab.Emotes.name
    }

    @Interface("Music Player", "music_player", "toplevel*")
    fun musicPlayer(player: Player) {
        player["tab", false] = Tab.MusicPlayer.name
    }

    @Interface("Notes", "notes", "toplevel*")
    fun notes(player: Player) {
        player["tab", false] = Tab.Notes.name
    }

    @Open("toplevel*")
    fun openGameFrame(player: Player) {
        for (name in list) {
            if (name.endsWith("_spellbook")) {
                val book = player["spellbook_config", 0] and 0x3
                player.open(
                    when (book) {
                        1 -> "ancient_spellbook"
                        2 -> "lunar_spellbook"
                        3 -> "dungeoneering_spellbook"
                        else -> name
                    },
                )
            } else {
                player.open(name)
            }
        }
    }

    @Refresh("toplevel*", "dialogue_npc*")
    fun refresh(player: Player) {
        player.interfaces.sendVisibility(player.interfaces.gameFrame, "wilderness_level", false)
        player.weakQueue("wild_level", 1, onCancel = null) {
            player.interfaces.sendVisibility(player.interfaces.gameFrame, "wilderness_level", false)
        }
    }

}

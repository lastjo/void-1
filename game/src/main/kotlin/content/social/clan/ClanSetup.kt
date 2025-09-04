package content.social.clan

import content.entity.player.dialogue.type.stringEntry
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.event.interfaceClose
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.client.ui.hasMenuOpen
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.chat.clan.Clan
import world.gregs.voidps.engine.entity.character.player.chat.clan.ClanRank
import world.gregs.voidps.engine.entity.character.player.chat.clan.LeaveClanChat
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.network.login.protocol.encode.leaveClanChat
import world.gregs.voidps.network.login.protocol.encode.updateClanChat
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open

class ClanSetup {

    @Interface("Clan Setup", "settings", "clan_chat")
    fun setup(player: Player) {
        if (player.hasMenuOpen()) {
            player.message("Please close the interface you have open before using Clan Chat setup.")
            return
        }
        player.open("clan_chat_setup")
    }

    @Open("clan_chat_setup")
    fun open(player: Player, id: String) {
        val clan = player.clan ?: player.ownClan ?: return
        player.interfaces.apply {
            sendText(id, "name", clan.name.ifBlank { "Chat disabled" })
            sendText(id, "enter", clan.joinRank.string)
            sendText(id, "talk", clan.talkRank.string)
            sendText(id, "kick", clan.kickRank.string)
            sendText(id, "loot", clan.lootRank.string)
        }
        player.sendVariable("coin_share_setting")
    }

    @Interface(component = "enter", id = "clan_chat_setup")
    fun enter(player: Player, id: String, component: String, option: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        val rank = ClanRank.from(option)
        if (rank == ClanRank.None) {
            return
        }
        clan.joinRank = rank
        player["clan_join_rank"] = rank.name
        player.interfaces.sendText(id, component, option)
        for (member in clan.members) {
            if (!clan.hasRank(member, rank)) {
                Publishers.all.publishPlayer(member, "leave_clan", true)
            }
        }
    }

    @Interface(component = "talk", id = "clan_chat_setup")
    fun talk(player: Player, id: String, component: String, option: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        val rank = ClanRank.from(option)
        if (rank == ClanRank.None) {
            return
        }
        clan.talkRank = rank
        player["clan_talk_rank"] = rank.name
        player.interfaces.sendText(id, component, option)
    }

    @Interface(component = "kick", id = "clan_chat_setup")
    fun kick(player: Player, id: String, component: String, option: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        val rank = ClanRank.from(option)
        if (rank.value <= ClanRank.Recruit.value) {
            return
        }
        clan.kickRank = rank
        player["clan_kick_rank"] = rank.name
        player.interfaces.sendText(id, component, option)
        updateUI(clan)
    }

    @Interface(component = "loot", id = "clan_chat_setup")
    fun loot(player: Player, id: String, component: String, option: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        val rank = ClanRank.from(option)
        if (rank == ClanRank.Anyone || rank == ClanRank.Owner) {
            return
        }
        clan.lootRank = rank
        player["clan_loot_rank"] = rank.name
        player.interfaces.sendText(id, component, option)
        player.softTimers.start("clan_loot_rank_update")
        player.message("Changes will take effect on your clan in the next 60 seconds.", ChatType.ClanChat)
    }

    @Interface(component = "coin_share", id = "clan_chat_setup")
    fun coinShare(player: Player) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        player.toggle("coin_share_setting")
        player.softTimers.start("clan_coin_share_update")
        player.message("Changes will take effect on your clan in the next 60 seconds.", ChatType.ClanChat)
    }

    @Interface("Set prefix", "name", "clan_chat_setup")
    suspend fun prefix(player: Player, id: String, component: String, option: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        val name = player.stringEntry("Enter chat prefix:")
        if (name.length > 12) {
            player.message("Name too long. A channel name cannot be longer than 12 characters.", ChatType.ClanChat)
            return
        }
        if (name.contains("mod", true) || name.contains("staff", true) || name.contains("admin", true)) {
            player.message("Name contains a banned word. Please try another name.", ChatType.ClanChat)
            return
        }
        clan.name = name
        player["clan_name"] = name
        player.interfaces.sendText(id, component, name)
        updateUI(clan)
    }

    @Close("clan_chat_setup")
    fun close(player: Player) {
        player.sendScript("clear_dialogues")
    }

    @Interface("Disable", "name", "clan_chat_setup")
    fun disable(player: Player, id: String, component: String) {
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            player.message("Only the clan chat owner can do this.", ChatType.ClanChat)
            return
        }
        clan.name = ""
        player["clan_name"] = ""
        player.interfaces.sendText(id, component, "Chat disabled")
        for (member in clan.members) {
            member.remove<Clan>("clan")
            member.clear("clan_chat")
            member.message("You have been kicked from the channel.", ChatType.ClanChat)
            member.client?.leaveClanChat()
        }
        clan.members.clear()
    }

    fun updateUI(clan: Clan) {
        val membersList = clan.members.map { ClanMember.of(it, clan.getRank(it)) }
        for (member in clan.members) {
            member.client?.updateClanChat(clan.ownerDisplayName, clan.name, clan.kickRank.value, membersList)
        }
    }
}

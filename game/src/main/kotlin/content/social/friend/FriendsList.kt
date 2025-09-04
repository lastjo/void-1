package content.social.friend

import content.social.chat.privateStatus
import content.social.clan.ClanLootShare
import content.social.clan.ClanMember
import content.social.clan.clan
import content.social.clan.ownClan
import content.social.ignore.ignores
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.Settings
import world.gregs.voidps.engine.data.definition.AccountDefinitions
import world.gregs.voidps.engine.entity.character.player.*
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.chat.clan.Clan
import world.gregs.voidps.engine.entity.character.player.chat.clan.ClanRank
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.network.client.instruction.FriendAdd
import world.gregs.voidps.network.client.instruction.FriendDelete
import world.gregs.voidps.network.login.protocol.encode.Friend
import world.gregs.voidps.network.login.protocol.encode.appendClanChat
import world.gregs.voidps.network.login.protocol.encode.leaveClanChat
import world.gregs.voidps.network.login.protocol.encode.sendFriendsList
import world.gregs.voidps.type.sub.*

class FriendsList(
    private val players: Players,
    private val accounts: AccountDefinitions,
    private val accountDefinitions: AccountDefinitions,
) {

    val maxFriends = 200

    @Spawn
    fun spawn(player: Player) {
        player.sendFriends()
        notifyBefriends(player, online = true)
    }

    @Despawn
    fun despawn(player: Player) {
        notifyBefriends(player, online = false)
    }

    @Interface(component = "private", id = "filter_buttons")
    fun private(player: Player, option: String) {
        if (player.privateStatus != "on" && option != "Off") {
            val next = option.lowercase()
            notifyBefriends(player, online = true) { it, current ->
                when {
                    current == "off" && next == "on" -> !player.ignores(it)
                    current == "off" && next == "friends" -> !it.isAdmin() && friends(player, it)
                    current == "friends" && next == "on" -> !friends(player, it) && !player.ignores(it)
                    else -> false
                }
            }
        } else if (player.privateStatus != "off" && option != "On") {
            val next = option.lowercase()
            notifyBefriends(player, online = false) { it, current ->
                when {
                    current == "friends" && next == "off" -> player.friend(it) && !it.isAdmin()
                    current == "on" && next == "friends" -> !friends(player, it)
                    current == "on" && next == "off" -> !it.isAdmin()
                    else -> false
                }
            }
        }
        player.privateStatus = option.lowercase()
    }

    @Instruction(FriendAdd::class)
    fun add(player: Player, instruction: FriendAdd) {
        val friendsName = instruction.friendsName
        val account = accounts.get(friendsName)
        if (account == null) {
            player.message("Unable to find player with name '$friendsName'.")
            return
        }

        if (player.name == friendsName) {
            player.message("You are already your own best friend!")
            return
        }

        if (player.ignores.contains(account.accountName)) {
            player.message("Please remove $friendsName from your ignore list first.")
            return
        }

        if (player.friends.size >= maxFriends) {
            player.message("Your friends list is full. Max of 100 for free users, and $maxFriends for members.")
            return
        }

        if (player.friends.contains(account.accountName)) {
            player.message("$friendsName is already on your friends list.")
            return
        }

        player.friends[account.accountName] = ClanRank.Friend
        if (player.privateStatus == "friends") {
            friendsName.updateFriend(player, online = true)
        }
        player.updateFriend(account)
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            return
        }
        val accountDefinition = accountDefinitions.get(friendsName) ?: return
        if (clan.members.any { it.accountName == accountDefinition.accountName }) {
            val target = players.get(friendsName) ?: return
            for (member in clan.members) {
                member.client?.appendClanChat(ClanMember.of(target, ClanRank.Friend))
            }
        }
    }

    @Instruction(FriendDelete::class)
    fun delete(player: Player, instruction: FriendDelete) {
        val friendsName = instruction.friendsName
        val account = accounts.get(friendsName)
        if (account == null || !player.friends.contains(account.accountName)) {
            player.message("Unable to find player with name '$friendsName'.")
            return
        }

        player.friends.remove(account.accountName)
        if (player.privateStatus == "friends") {
            friendsName.updateFriend(player, online = false)
        }
        val clan = player.clan ?: player.ownClan ?: return
        if (!clan.hasRank(player, ClanRank.Owner)) {
            return
        }
        val accountDefinition = accountDefinitions.get(friendsName) ?: return
        if (clan.members.any { it.accountName == accountDefinition.accountName }) {
            val target = players.get(friendsName) ?: return
            for (member in clan.members) {
                member.client?.appendClanChat(ClanMember.of(target, ClanRank.None))
            }
            if (!clan.hasRank(target, clan.joinRank)) {
                Publishers.all.publishPlayer(target, "leave_clan", true)
            }
        }
    }

    @Subscribe("leave_clan")
    fun leaveClan(player: Player, id: Any) {
        val forced = id as? Boolean ?: false
        val clan: Clan? = player.remove("clan")
        player.clear("clan_chat")
        player.message("You have ${if (forced) "been kicked from" else "left"} the channel.", ChatType.ClanChat)
        if (clan != null) {
            player.client?.leaveClanChat()
            clan.members.remove(player)
            for (member in clan.members) {
                if (member != player) {
                    member.client?.appendClanChat(ClanMember.of(player, ClanRank.Anyone))
                }
            }
            if (player.accountName != clan.owner || player.isAdmin()) {
                player.sendFriends()
            }
            ClanLootShare.update(player, clan, lootShare = false)
        }
    }

    fun friends(player: Player) = { other: Player, status: String ->
        when (status) {
            "friends" -> friends(player, other)
            "off" -> other.isAdmin()
            "on" -> !player.ignores(other)
            else -> false
        }
    }

    fun friends(player: Player, it: Player) = player.friend(it) || it.isAdmin()

    fun Player.sendFriends() {
        client?.sendFriendsList(friends.mapNotNull { toFriend(this, accounts.getByAccount(it.key) ?: return@mapNotNull null) })
    }

    fun notifyBefriends(player: Player, online: Boolean, notify: (Player, String) -> Boolean = friends(player)) {
        players
            .filter { it.friend(player) && notify(it, player.privateStatus) }
            .forEach { friend ->
                friend.updateFriend(
                    Friend(
                        name = player.name,
                        previousName = player.previousName,
                        rank = (friend.friends[player.accountName] ?: ClanRank.Friend).value,
                        world = if (online) Settings.world else 0,
                        worldName = Settings.worldName,
                    ),
                )
            }
    }

    fun String.updateFriend(friend: Player, online: Boolean) {
        val player = players.get(this) ?: return
        player.updateFriend(
            Friend(
                name = friend.name,
                previousName = friend.previousName,
                rank = (player.friends[friend.accountName] ?: ClanRank.Friend).value,
                world = if (online) Settings.world else 0,
                worldName = Settings.worldName,
            ),
        )
    }
}

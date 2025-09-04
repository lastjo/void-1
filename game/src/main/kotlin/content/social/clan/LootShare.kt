package content.social.clan

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.chat.clan.ClanRank
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.type.TimerState
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Spawn
import world.gregs.voidps.type.sub.TimerStart
import world.gregs.voidps.type.sub.TimerTick
import java.util.concurrent.TimeUnit

class LootShare {

    @Spawn
    fun spawn(player: Player) {
        player.sendVariable("loot_share")
    }

    @Interface(component = "loot_share", id = "clan_chat")
    suspend fun toggle(player: Player) {
        val clan = player.clan ?: return
        if (clan.lootRank == ClanRank.None) {
            player.message("LootShare is disabled by the clan owner.", ChatType.ClanChat)
            return
        }
        if (!clan.hasRank(player, clan.lootRank)) {
            player.message("Only ${clan.lootRank.name.lowercase()}s can share loot.", ChatType.ClanChat)
            return
        }
        player["loading_loot_share"] = true
        player.softTimers.start("clan_loot_update")
        val lootShare = player["loot_share", false]
        player.message("You will ${if (lootShare) "stop sharing" else "be able to share"} loot in 2 minutes.", ChatType.ClanChat)
    }

    @TimerStart("clan_loot_update")
    fun start(player: Player): Int = TimeUnit.MINUTES.toTicks(2)

    @TimerTick("clan_loot_update")
    fun lootUpdate(player: Player): Int {
        player["loading_loot_share"] = false
        val clan = player.clan ?: return TimerState.CANCEL
        val lootShare = player.toggle("loot_share")
        ClanLootShare.update(player, clan, lootShare)
        return TimerState.CANCEL
    }

    @TimerStart("clan_loot_rank_update", "clan_coin_share_update")
    fun startUpdate(player: Player): Int = TimeUnit.SECONDS.toTicks(30)

    @TimerTick("clan_loot_rank_update")
    fun rankUpdate(player: Player): Int {
        val clan = player.clan ?: player.ownClan ?: return TimerState.CANCEL
        clan.lootRank = ClanRank.valueOf(player["clan_loot_rank", "None"])
        for (member in clan.members) {
            if (clan.hasRank(member, clan.lootRank) || !member["loot_share", false]) {
                continue
            }
            ClanLootShare.update(player, clan, lootShare = false)
        }
        return TimerState.CANCEL
    }

    @TimerTick("clan_coin_share_update")
    fun coinShareUpdate(player: Player): Int {
        val clan = player.clan ?: player.ownClan ?: return TimerState.CANCEL
        clan.coinShare = player["coin_share_setting", false]
        for (member in clan.members) {
            member["coin_share"] = clan.coinShare
            member.message("CoinShare has been switched ${if (clan.coinShare) "on" else "off"}.", ChatType.ClanChat)
        }
        return TimerState.CANCEL
    }
}

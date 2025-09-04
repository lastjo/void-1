package content.social.chat

import content.social.clan.chatType
import content.social.clan.clan
import content.social.ignore.ignores
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.cache.secure.Huffman
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.update.view.Viewport.Companion.VIEW_RADIUS
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.name
import world.gregs.voidps.engine.entity.character.player.rights
import world.gregs.voidps.network.client.instruction.ChatPrivate
import world.gregs.voidps.network.client.instruction.ChatPublic
import world.gregs.voidps.network.client.instruction.ChatTypeChange
import world.gregs.voidps.network.login.protocol.encode.clanChat
import world.gregs.voidps.network.login.protocol.encode.privateChatFrom
import world.gregs.voidps.network.login.protocol.encode.privateChatTo
import world.gregs.voidps.network.login.protocol.encode.publicChat
import world.gregs.voidps.type.sub.Instruction

class Chat(
    private val players: Players,
    private val huffman: Huffman,
) {

    @Instruction(ChatPrivate::class)
    fun private(player: Player, instruction: ChatPrivate) {
        val target = players.get(instruction.friend)
        if (target == null || target.ignores(player)) {
            player.message("Unable to send message - player unavailable.")
            return
        }
        val compressed = huffman.compress(instruction.message)
        player.client?.privateChatTo(target.name, compressed)
        target.client?.privateChatFrom(player.name, player.rights.ordinal, compressed)
    }

    @Instruction(ChatTypeChange::class)
    fun typeChange(player: Player, instruction: ChatTypeChange) {
        player["chat_type"] = when (instruction.type) {
            1 -> "clan"
            else -> "public"
        }
    }

    @Instruction(ChatPublic::class)
    fun public(player: Player, instruction: ChatPublic) {
        val text = if (instruction.text.all { it.isUpperCase() }) {
            instruction.text.toTitleCase()
        } else {
            instruction.text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        when (player.chatType) {
            "public" -> {
                val compressed = huffman.compress(text)
                players.filter { it.tile.within(player.tile, VIEW_RADIUS) && !it.ignores(player) }.forEach {
                    it.client?.publicChat(player.index, instruction.effects, player.rights.ordinal, compressed)
                }
            }
            "clan" -> {
                val clan = player.clan
                if (clan == null) {
                    player.message("You must be in a clan chat to talk.", ChatType.ClanChat)
                    return
                }
                if (!clan.hasRank(player, clan.talkRank) || !clan.members.contains(player)) {
                    player.message("You are not allowed to talk in this clan chat.", ChatType.ClanChat)
                    return
                }
                val compressed = huffman.compress(text)
                clan.members.filterNot { it.ignores(player) }.forEach {
                    it.client?.clanChat(player.name, player.clan!!.name, player.rights.ordinal, compressed)
                }
            }
        }
    }
}

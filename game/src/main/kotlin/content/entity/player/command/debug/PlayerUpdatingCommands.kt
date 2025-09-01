package content.entity.player.command.debug

import content.bot.isBot
import content.entity.combat.hit.damage
import content.entity.effect.transform
import content.entity.proj.shoot
import net.pearx.kasechange.toScreamingSnakeCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.event.adminCommand
import world.gregs.voidps.engine.client.ui.event.modCommand
import world.gregs.voidps.engine.entity.character.*
import world.gregs.voidps.engine.entity.character.player.*
import world.gregs.voidps.engine.get
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.map.zone.DynamicZones
import world.gregs.voidps.type.Delta
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Command

class PlayerUpdatingCommands(val players: Players) {

    @Command("kill", description = "remove all bots", rights = PlayerRights.ADMIN)
    fun kill(player: Player) {
        val it = players.iterator()
        val remove = mutableListOf<Player>()
        while (it.hasNext()) {
            val p = it.next()
            if (p.isBot) {
                remove.add(p)
            }
        }
        for (bot in remove) {
            players.remove(bot)
        }
    }

    @Command("players", description = "get the total and local player counts", rights = PlayerRights.MOD)
    fun players(player: Player) {
        player.message("Players: ${players.size}, ${player.viewport?.players?.localCount}")
    }

    @Command("anim (anim-id)", description = "perform animation by int or string id (-1 to clear)", rights = PlayerRights.ADMIN)
    fun anim(player: Player, content: String) {
        when (content) {
            "-1", "" -> player.clearAnim()
            else -> player.anim(content, override = true) // 863
        }
    }

    @Command("emote (emote-id)", description = "perform render emote by int or string id (-1 to clear)", rights = PlayerRights.ADMIN)
    fun emote(player: Player, content: String) {
        when (content) {
            "-1", "" -> player.clearRenderEmote()
            else -> player.renderEmote(content)
        }
    }

    @Command("gfx (gfx-id)", description = "perform graphic effect by int or string id (-1 to clear)", rights = PlayerRights.ADMIN)
    fun gfx(player: Player, content: String) {
        when (content) {
            "-1", "" -> player.clearGfx()
            else -> player.gfx(content) // 93
        }
    }

    @Command("proj (gfx-id)", description = "shoot projectile by int or string id (-1 to clear)", rights = PlayerRights.ADMIN)
    fun proj(player: Player, content: String) {
        player.shoot(content, player.tile.add(0, 5), delay = 0, flightTime = 400)
    }

    @Command("tfm", "transform", description = "transform to npc with int or string id (-1 to clear)", rights = PlayerRights.ADMIN)
    fun transform(player: Player, content: String) {
        player.transform(content)

    }

    @Command("overlay", rights = PlayerRights.ADMIN)
    fun overlay(player: Player) {
        player.colourOverlay(-2108002746, 10, 100)
    }

    @Command("chat (message)", description = "force a chat message over players head", rights = PlayerRights.ADMIN)
    fun chat(player: Player, content: String) {
        player.say(content)
    }

    @Command("move", rights = PlayerRights.ADMIN)
    fun move(player: Player) {
        val move = player.visuals.exactMovement
        move.startX = -4
        move.startY = 2
        move.startDelay = 0
        move.endX = 0
        move.endY = 0
        move.endDelay = 100
        move.direction = Direction.EAST.ordinal
        player.flagExactMovement()
    }

    @Command("hit [amount]", description = "damage player by an amount", rights = PlayerRights.ADMIN)
    fun hit(player: Player, content: String) {
        player.damage(content.toIntOrNull() ?: 10)
    }

    @Command("time", rights = PlayerRights.ADMIN)
    fun time(player: Player) {
        player.setTimeBar(true, 0, 60, 1)
    }

    @Command("watch (player-name)", description = "watch another player", rights = PlayerRights.ADMIN)
    fun watch(player: Player, content: String) {
        val bot = players.get(content)
        if (bot != null) {
            player.watch(bot)
        } else {
            player.clearWatch()
        }
    }

    @Command("shoot", rights = PlayerRights.ADMIN)
    fun shoot(player: Player) {
        player.shoot("15", player.tile.addY(10))
    }

    @Command("face (delta-x) (delta-y)", description = "turn player to face a direction or delta coordinate", rights = PlayerRights.ADMIN)
    fun face(player: Player, content: String) {
        if (content.contains(" ")) {
            val parts = content.split(" ")
            player.face(Delta(parts[0].toInt(), parts[1].toInt()))
        } else {
            val direction = Direction.valueOf(content.toScreamingSnakeCase())
            player.face(direction.delta)
        }
    }

    @Command("zone", "chunk", rights = PlayerRights.ADMIN)
    fun zone(player: Player) {
        val zones: DynamicZones = get()
        zones.copy(player.tile.zone, player.tile.zone, rotation = 2)
    }

    @Command("clear_zone", description = "clear the dynamic flag from current zone", rights = PlayerRights.ADMIN)
    fun clearZone(player: Player) {
        val zones: DynamicZones = get()
        zones.clear(player.tile.zone)
    }

    @Command("skill (level)", description = "set the current displayed skill level", rights = PlayerRights.ADMIN)
    fun skill(player: Player, content: String) {
        player.skillLevel = content.toInt()
    }

    @Command("cmb (level)", description = "set the current displayed combat level", rights = PlayerRights.ADMIN)
    fun combat(player: Player, content: String) {
        player.combatLevel = content.toInt()
    }

    @Command("tgl", description = "toggle skill level display", rights = PlayerRights.ADMIN)
    fun toggle(player: Player) {
        player.toggleSkillLevel()
    }

    @Command("sum (level)", description = "set the current summoning combat level", rights = PlayerRights.ADMIN)
    fun sum(player: Player, content: String) {
        player.summoningCombatLevel = content.toInt()
    }
}

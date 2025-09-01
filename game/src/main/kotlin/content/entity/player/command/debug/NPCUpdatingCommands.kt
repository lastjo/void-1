package content.entity.player.command.debug

import content.entity.effect.transform
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.event.adminCommand
import world.gregs.voidps.engine.client.ui.event.modCommand
import world.gregs.voidps.engine.entity.character.*
import world.gregs.voidps.engine.entity.character.move.running
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.inject
import world.gregs.voidps.network.login.protocol.visual.update.HitSplat
import world.gregs.voidps.type.Delta
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Command

class NPCUpdatingCommands(val npcs: NPCs) {

    @Command("npckill", description = "kill all npcs", rights = PlayerRights.ADMIN)
    fun npckill(player: Player) {
        npcs.forEach { npc ->
            npcs.remove(npc)
        }
    }

    @Command("npcs", description = "get total npc count", rights = PlayerRights.MOD)
    fun npcs(player: Player) {
        player.message("NPCs: ${npcs.count()}")
    }

    @Command("npctfm", rights = PlayerRights.ADMIN)
    fun transform(player: Player, content: String) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.transform(content)
    }

    @Command("npcturn", rights = PlayerRights.ADMIN)
    fun turn(player: Player, content: String) {
        val npc = npcs[player.tile.addY(1)].first()
        val parts = content.split(" ")
        npc.face(Delta(parts[0].toInt(), parts[1].toInt()))
    }

    @Command("npcanim", rights = PlayerRights.ADMIN)
    fun anim(player: Player, content: String) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.anim(content) // 863
    }

    @Command("npcoverlay", rights = PlayerRights.ADMIN)
    fun overlay(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.colourOverlay(-2108002746, 10, 100)
    }

    @Command("npcchat", rights = PlayerRights.ADMIN)
    fun chat(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.say("Testing")
    }

    @Command("npcgfx", rights = PlayerRights.ADMIN)
    fun gfx(player: Player, content: String) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.gfx(content) // 93
    }

    @Command("npchit", rights = PlayerRights.ADMIN)
    fun hit(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.visuals.hits.splats.add(HitSplat(10, HitSplat.Mark.Healed, npc.levels.getPercent(Skill.Constitution, fraction = 255.0).toInt()))
        npc.flagHits()
    }

    @Command("npctime", rights = PlayerRights.ADMIN)
    fun time(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.setTimeBar(true, 0, 60, 1)
    }

    @Command("npcwatch", rights = PlayerRights.ADMIN)
    fun watch(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.watch(player)
    }

    @Command("npccrawl", rights = PlayerRights.ADMIN)
    fun crawl(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        //    npc.def["crawl"] = true
        //    npc.walkTo(npc.tile)
        //    npc.movement.steps.add(Tile(npc.tile.x, npc.tile.y + 1))
    }

    @Command("npcrun", rights = PlayerRights.ADMIN)
    fun run(player: Player) {
        val npc = npcs[player.tile.addY(1)].first()
        npc.running = true
        //    npc.walkTo(npc.tile)
        //    npc.movement.steps.add(Tile(npc.tile.x, npc.tile.y + 2))
    }
}

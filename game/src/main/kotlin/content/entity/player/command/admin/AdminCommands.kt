@file:Suppress("UNCHECKED_CAST")

package content.entity.player.command.admin

import content.bot.interact.navigation.graph.NavigationGraph
import content.entity.npc.shop.openShop
import content.entity.obj.ObjectTeleports
import content.entity.obj.ship.CharterShips
import content.entity.player.combat.special.MAX_SPECIAL_ATTACK
import content.entity.player.combat.special.specialAttackEnergy
import content.entity.player.dialogue.type.choice
import content.entity.player.effect.energy.MAX_RUN_ENERGY
import content.entity.player.effect.skull
import content.entity.player.effect.unskull
import content.entity.player.modal.book.Books
import content.entity.sound.jingle
import content.entity.sound.midi
import content.entity.sound.sound
import content.entity.world.music.MusicTracks
import content.entity.world.music.MusicUnlock
import content.quest.member.fairy_tale_part_2.fairy_ring.FairyRingCodes
import content.quest.quests
import content.quest.refreshQuestJournal
import content.skill.prayer.PrayerConfigs
import content.skill.prayer.PrayerConfigs.PRAYERS
import content.skill.prayer.isCurses
import content.social.trade.exchange.GrandExchange
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.coroutines.*
import net.pearx.kasechange.toSentenceCase
import net.pearx.kasechange.toSnakeCase
import world.gregs.voidps.cache.Definition
import world.gregs.voidps.cache.definition.Extra
import world.gregs.voidps.engine.client.PlayerAccountLoader
import world.gregs.voidps.engine.client.clearCamera
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.*
import world.gregs.voidps.engine.client.ui.close
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.client.ui.playTrack
import world.gregs.voidps.engine.client.variable.start
import world.gregs.voidps.engine.data.*
import world.gregs.voidps.engine.data.definition.*
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.npc.loadNpcSpawns
import world.gregs.voidps.engine.entity.character.player.*
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.Experience
import world.gregs.voidps.engine.entity.character.player.skill.level.Level
import world.gregs.voidps.engine.entity.character.player.skill.level.Levels
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.drop.DropTable
import world.gregs.voidps.engine.entity.item.drop.DropTables
import world.gregs.voidps.engine.entity.item.drop.ItemDrop
import world.gregs.voidps.engine.entity.item.drop.TableType
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.item.floor.ItemSpawns
import world.gregs.voidps.engine.entity.item.floor.loadItemSpawns
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.entity.obj.loadObjectSpawns
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.get
import world.gregs.voidps.engine.inv.*
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.charge
import world.gregs.voidps.engine.inv.transact.operation.AddItemLimit.addToLimit
import world.gregs.voidps.engine.queue.queue
import world.gregs.voidps.engine.queue.softQueue
import world.gregs.voidps.engine.timer.toTicks
import world.gregs.voidps.network.login.protocol.encode.playJingle
import world.gregs.voidps.network.login.protocol.encode.playMIDI
import world.gregs.voidps.network.login.protocol.encode.playSoundEffect
import world.gregs.voidps.network.login.protocol.encode.systemUpdate
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.Region
import world.gregs.voidps.type.sub.Command
import world.gregs.voidps.type.sub.Spawn
import java.util.concurrent.TimeUnit
import kotlin.collections.set
import kotlin.system.measureTimeMillis

class AdminCommands(
    private val areas: AreaDefinitions,
    private val players: Players,
    private val exchange: GrandExchange,
    private val definitions: ItemDefinitions,
    private val enums: EnumDefinitions,
    private val itemDefinitions: ItemDefinitions,
    private val tables: DropTables,
    private val accountLoader: PlayerAccountLoader,
) {

    val alternativeNames = Object2ObjectOpenHashMap<String, String>()

    val utf8Regex = "[^\\x20-\\x7e]".toRegex()

    init {
        world.gregs.voidps.engine.client.ui.event.OldCommand.adminCommands.add("${Colours.PURPLE.toTag()}====== Admin Commands ======</col>")
        world.gregs.voidps.engine.client.ui.event.OldCommand.adminCommands.add("")
    }

    @Command("tele (x) (y) [level]", "tp", description = "teleport to given coordinates or area name", rights = PlayerRights.ADMIN)
    fun tele(player: Player, content: String) {
        if (content.contains(",")) {
            val params = content.split(",")
            val level = params[0].toInt()
            val x = params[1].toInt() shl 6 or params[3].toInt()
            val y = params[2].toInt() shl 6 or params[4].toInt()
            player.tele(x, y, level)
        } else {
            val parts = content.split(" ")
            val int = parts[0].toIntOrNull()
            when {
                int == null -> when (content.lowercase()) {
                    "draynor" -> player.tele(3086, 3248, 0)
                    "varrock" -> player.tele(3212, 3429, 0)
                    "lumbridge", "lumb", "lummy" -> player.tele(3222, 3219, 0)
                    "burthorpe" -> player.tele(2899, 3546, 0)
                    "falador" -> player.tele(2966, 3379, 0)
                    "barbarian_village", "barb_village" -> player.tele(3084, 3421, 0)
                    "al_kharid", "alkharid" -> player.tele(3293, 3183, 0)
                    "canifis" -> player.tele(3474, 3475, 0)
                    "ge", "grand_exchange" -> player.tele(3164, 3484, 0)
                    else -> player.tele(areas[content])
                }
                parts.size == 1 -> player.tele(Region(int).tile.add(32, 32))
                else -> player.tele(int, parts[1].toInt(), if (parts.size > 2) parts[2].toInt() else 0)
            }
        }
        player["world_map_centre"] = player.tile.id
        player["world_map_marker_player"] = player.tile.id
    }

    @Command("teleto (player-name)", description = "teleport to another player", rights = PlayerRights.ADMIN)
    fun teleTo(player: Player, content: String) {
        val target = players.firstOrNull { it.name.equals(content, true) }
        if (target != null) {
            player.tele(target.tile)
        }
    }

    @Command("teletome (player-name)", description = "teleport another player to you", rights = PlayerRights.ADMIN)
    fun teleToMe(player: Player, content: String) {
        val other = players.get(content) ?: return
        other.tele(player.tile)
    }

    @Command("npc (npc-id)", description = "spawn an npc", rights = PlayerRights.ADMIN)
    fun npc(player: Player, content: String) {
        val id = content.toIntOrNull()
        val defs: NPCDefinitions = get()
        val definition = if (id != null) defs.getOrNull(id) else defs.getOrNull(content)
        if (definition == null) {
            player.message("Unable to find npc with id $content.")
            return
        }
        val npcs: NPCs = get()
        println(
            """
                - name: $content
                  x: ${player.tile.x}
                  y: ${player.tile.y}
                  level: ${player.tile.level}
            """.trimIndent(),
        )
        val npc = npcs.add(definition.stringId, player.tile, Direction.NORTH)
        npc.start("movement_delay", -1)
    }

    @Command("save", description = "save all players", rights = PlayerRights.MOD)
    fun save(player: Player, content: String) {
        val account: SaveQueue = get()
        players.forEach(account::save)
        exchange.save()
    }

    @Spawn
    fun spawn(world: World) {
        for (id in 0 until definitions.size) {
            val definition = definitions.get(id)
            val list = (definition.extras as? MutableMap<String, Any>)?.remove("aka") as? List<String> ?: continue
            for (name in list) {
                alternativeNames[name] = definition.stringId
            }
        }
    }

    @Command("items (item-id) [item-id] [item-id]...", description = "spawn multiple items at once", rights = PlayerRights.ADMIN)
    fun items(player: Player, content: String) {
        val parts = content.split(" ")
        for (i in parts.indices) {
            val id = definitions.get(alternativeNames.getOrDefault(parts[i], parts[i])).stringId
            player.inventory.add(id)
        }
    }

    @Command("item (item-id) [item-amount]", description = "spawn an item by int or string id e.g. 'item pure_ess 2'", rights = PlayerRights.ADMIN)
    fun item(player: Player, content: String) {
        val parts = content.split(" ")
        val definition = definitions.get(alternativeNames.getOrDefault(parts[0], parts[0]))
        val id = definition.stringId
        val amount = parts.getOrNull(1) ?: "1"
        val charges = definition.getOrNull<Int>("charges")
        player.inventory.transaction {
            if (charges != null) {
                for (i in 0 until amount.toSILong()) {
                    val index = inventory.freeIndex()
                    if (index == -1) {
                        break
                    }
                    set(index, Item(id, 1))
                    if (charges > 0) {
                        charge(player, index, charges)
                    }
                }
            } else {
                addToLimit(id, if (amount == "max") Int.MAX_VALUE else amount.toSILong().toInt())
            }
        }
        if (player.inventory.transaction.error != TransactionError.None) {
            player.message(player.inventory.transaction.error.toString())
        }
    }

    @Command("give (item-id) (amount) (player-name)", description = "spawn item in another players inventory", rights = PlayerRights.ADMIN)
    fun give(player: Player, content: String) {
        val parts = content.split(" ")
        val id = definitions.get(parts.first()).stringId
        val amount = parts[1]
        val name = content.removePrefix("${parts[0]} ${parts[1]} ")
        val target = players.get(name)
        if (target == null) {
            player.message("Couldn't find player $name")
        } else {
            target.inventory.add(id, if (amount == "max") Int.MAX_VALUE else amount.toSILong().toInt())
        }
    }

    @Command("find (content-name)", "search", description = "search for a piece of content by name", rights = PlayerRights.MOD)
    fun find(player: Player, content: String) {
        val search = content.lowercase()
        var found = 0
        player.message("===== Items =====", ChatType.Console)
        found += search(player, get<ItemDefinitions>(), search) { it.name }
        player.message("===== Objects =====", ChatType.Console)
        found += search(player, get<ObjectDefinitions>(), search) { it.name }
        player.message("===== NPCs =====", ChatType.Console)
        found += search(player, get<NPCDefinitions>(), search) { it.name }
        player.message("===== Commands =====", ChatType.Console)
        for (command in world.gregs.voidps.engine.client.ui.event.OldCommand.adminCommands) {
            if (command.startsWith(Colours.BLUE.toTag()) && command.contains(content, ignoreCase = true)) {
                val colourless = command.removePrefix(Colours.BLUE.toTag()).removeSuffix("</col>")
                val cmd = colourless.substringBefore("(").substringBefore("[").trim()
                player.message("[$cmd] - usage: $colourless", ChatType.Console)
                found++
            }
        }
        player.message("$found results found for '$search'", ChatType.Console)
    }

    @Command("clear", description = "delete all items in the players inventory", rights = PlayerRights.MOD)
    fun clear(player: Player, content: String) {
        player.queue("question") {
            choice("Are you sure you want to delete all inv items?") {
                option("Yes I'm sure") {
                    player.inventory.clear()
                }
                option("No thanks")
            }
        }
    }

    @Command("master", description = "set all skills to 99", rights = PlayerRights.ADMIN)
    fun master(player: Player, content: String) {
        for (skill in Skill.all) {
            player.experience.set(skill, 14000000.0)
            player.levels.restore(skill, 1000)
        }
        player.softQueue("", 1) {
            player.clear("skill_stat_flash")
        }
    }

    @Command("unlock [activity-type]", description = "unlock everything or of a type (music, tasks, emotes, quests)", rights = PlayerRights.ADMIN)
    fun unlock(player: Player, content: String) {
        val type = content
        if (type == "" || type == "music" || type == "songs" || type == "music tracks" || type == "music_tracks") {
            get<EnumDefinitions>().get("music_track_names").map?.keys?.forEach { key ->
                MusicUnlock.unlockTrack(player, key)
            }
            player.message("All songs unlocked.")
        }
        if (type == "" || type == "tasks" || type == "achievements") {
            for (struct in get<StructDefinitions>().definitions) {
                if (struct.stringId.endsWith("_task")) {
                    player[struct.stringId] = true
                }
            }
            player.message("All tasks completed.")
        }
        if (type == "" || type == "emotes") {
            for (component in listOf(
                "glass_box", "climb_rope", "lean", "glass_wall", "idea", "stomp", "flap", "slap_head", "zombie_walk", "zombie_dance",
                "zombie_hand", "scared", "bunny_hop", "snowman_dance", "air_guitar", "safety_first", "explore", "trick", "freeze", "give_thanks",
                "around_the_world_in_eggty_days", "dramatic_point", "faint", "puppet_master", "taskmaster", "seal_of_approval",
            )) {
                player["unlocked_emote_$component"] = true
            }
            player["unlocked_emote_lost_tribe"] = true
            player.message("All emotes unlocked.")
        }
        if (type == "" || type == "quests") {
            for (quest in quests) {
                player[quest] = "completed"
            }
            player["quest_points"] = player["quest_points_total", 1]
            player.refreshQuestJournal()
            player.message("All quests unlocked.")
        }
    }

    @Command("setlevel (skill-name) (level)", description = "set any skill to a specific level", rights = PlayerRights.ADMIN)
    fun setLevel(player: Player, content: String) {
        val split = content.split(" ")
        val skill = Skill.valueOf(split[0].toSentenceCase())
        val level = split[1].toInt()
        val target = if (split.size > 2) {
            val name = content.removeSuffix("${split[0]} ${split[1]} ")
            players.get(name)
        } else {
            player
        }
        if (target == null) {
            println("Unable to find target.")
        } else {
            target.experience.set(skill, Level.experience(skill, level))
            player.levels.set(skill, level)
            player.softQueue("", 1) {
                target.removeVarbit("skill_stat_flash", skill.name.lowercase())
            }
        }
    }

    @Command("reset", description = "rest all skills to level 1", rights = PlayerRights.ADMIN)
    fun reset(player: Player, content: String) {
        for ((index, skill) in Skill.all.withIndex()) {
            player.experience.set(skill, Experience.defaultExperience[index])
            player.levels.set(skill, Levels.defaultLevels[index])
        }
        player[if (player.isCurses()) PrayerConfigs.QUICK_CURSES else PrayerConfigs.QUICK_PRAYERS] = emptyList<Any>()
        player["xp_counter"] = 0.0
        player.clearCamera()
    }

    @Command("hide", description = "toggle invisibility to other players", rights = PlayerRights.MOD)
    fun hide(player: Player, content: String) {
        player.appearance.hidden = !player.appearance.hidden
        player.flagAppearance()
    }

    @Command("skull", description = "apply a skull to your player", rights = PlayerRights.ADMIN)
    fun skull(player: Player, content: String) {
        player.skull()
    }

    @Command("unskull", description = "remove your skull", rights = PlayerRights.ADMIN)
    fun unskull(player: Player, content: String) {
        player.unskull()
    }

    @Command("rest", description = "set run energy to full", rights = PlayerRights.ADMIN)
    fun rest(player: Player, content: String) {
        player["energy"] = MAX_RUN_ENERGY
    }

    @Command("spec", description = "set special attack energy to full", rights = PlayerRights.ADMIN)
    fun spec(player: Player, content: String) {
        player.specialAttackEnergy = MAX_SPECIAL_ATTACK
    }

    @Command("pray", description = "restore full prayer points", rights = PlayerRights.ADMIN)
    fun pray(player: Player, content: String) {
        Skill.entries.forEach {
            player.levels.clear(it)
        }
    }

    @Command("restore", description = "restore all skills", rights = PlayerRights.ADMIN)
    fun restore(player: Player, content: String) {
        player.levels.clear(Skill.Prayer)
    }

    @Command("curse", "curses", description = "toggle curse prayers", rights = PlayerRights.ADMIN)
    fun curses(player: Player, content: String) {
        player[PRAYERS] = if (player.isCurses()) "normal" else "curses"
    }

    @Command("ancient", "ancients", description = "open ancient spellbook", rights = PlayerRights.ADMIN)
    fun ancients(player: Player, content: String) {
        player.open("ancient_spellbook")
    }

    @Command("lunar", "lunars", description = "open lunar spellbook", rights = PlayerRights.ADMIN)
    fun lunars(player: Player, content: String) {
        player.open("lunar_spellbook")
    }

    @Command("regular", "regulars", "normal", "modern", "moderns", description = "open modern spellbook", rights = PlayerRights.ADMIN)
    fun regulars(player: Player, content: String) {
        player.open("modern_spellbook")
    }

    @Command("dung", "dungs", "dungeoneering", "dungeoneerings", description = "open dungeoneering spellbook", rights = PlayerRights.ADMIN)
    fun dung(player: Player, content: String) {
        player.open("dungeoneering_spellbook")
    }

    @Command("sound (sound-id)", description = "play a sound by int or string id", rights = PlayerRights.MOD)
    fun sound(player: Player, content: String) {
        val id = content.toIntOrNull()
        if (id == null) {
            player.sound(content.toSnakeCase())
        } else {
            player.client?.playSoundEffect(id)
        }
    }

    @Command("midi (midi-id)", description = "play a midi effect by int or string id", rights = PlayerRights.MOD)
    fun midi(player: Player, content: String) {
        val id = content.toIntOrNull()
        if (id == null) {
            player.midi(content.toSnakeCase())
        } else {
            player.client?.playMIDI(id)
        }
    }

    @Command("jingle (jingle-id)", description = "play a jingle sound by int or string id", rights = PlayerRights.MOD)
    fun jingle(player: Player, content: String) {
        val id = content.toIntOrNull()
        if (id == null) {
            player.jingle(content.toSnakeCase())
        } else {
            player.client?.playJingle(id)
        }
    }

    @Command("song (song-id)", "track", description = "play a song by int id", rights = PlayerRights.MOD)
    fun song(player: Player, content: String) {
        val names = enums.get("music_track_names").map!!
        var id = content.toIntOrNull()
        if (id == null) {
            val search = content.replace(" ", "_")
            for ((key, value) in names) {
                if ((value as String).toSnakeCase() == search) {
                    id = key
                    break
                }
            }
            if (id != null) {
                player.playTrack(id)
            } else {
                player.message("Song not found with id '$search'.")
            }
        } else {
            player.playTrack(content.toInt())
        }
    }

    @Command("pos", "mypos", "tile", description = "print out players current coordinates", rights = PlayerRights.MOD)
    fun position(player: Player) {
        player.message("${player.tile} Zone(${player.tile.zone.id}) ${player.tile.region}")
        println(player.tile)
    }

    @Command("reload (config-name)", description = "reload any type of content or file e.g. npcs, object defs, or settings", rights = PlayerRights.ADMIN)
    fun reload(player: Player, content: String) {
        val files = configFiles()
        when (content) {
            "book", "books" -> get<Books>().load(files.list(Settings["definitions.books"]))
            "stairs", "tele", "teles", "teleports" -> get<ObjectTeleports>().load(files.list(Settings["map.teleports"]))
            "tracks", "songs" -> get<MusicTracks>().load(files.find(Settings["map.music"]))
            "fairy ring", "fairy codes" -> get<FairyRingCodes>().load(files.find(Settings["definitions.fairyCodes"]))
            "ships" -> get<CharterShips>().load(files.find(Settings["map.ships.prices"]))
            "objects", "objs" -> {
                val defs: ObjectDefinitions = get()
                val custom: GameObjects = get()
                defs.load(files.list(Settings["definitions.objects"]))
                loadObjectSpawns(custom, files.list(Settings["spawns.objects"]), defs)
            }
            "item defs", "items", "floor items" -> {
                val items: FloorItems = get()
                val itemSpawns: ItemSpawns = get()
                items.clear()
                itemDefinitions.load(files.list(Settings["definitions.items"]))
                loadItemSpawns(items, itemSpawns, files.list(Settings["spawns.items"]), definitions)
            }
            "nav graph", "ai graph" -> get<NavigationGraph>().load(files.find(Settings["map.navGraph"]))
            "npcs" -> {
                val npcDefs = get<NPCDefinitions>()
                npcDefs.load(files.list(Settings["definitions.npcs"]))
                val npcs: NPCs = get()
                loadNpcSpawns(npcs, files.list(Settings["spawns.npcs"]), npcDefs)
            }
            "areas" -> get<AreaDefinitions>().load(files.list(Settings["map.areas"]))
            "object defs" -> get<ObjectDefinitions>().load(files.list(Settings["definitions.objects"]))
            "emotes", "render anims", "render emotes" -> get<RenderEmoteDefinitions>().load(files.find(Settings["definitions.renderEmotes"]))
            "anim defs", "anims" -> get<AnimationDefinitions>().load(files.list(Settings["definitions.animations"]))
            "container defs", "containers", "inventory defs", "inventories", "inv defs", "invs", "shop", "shops" -> {
                get<InventoryDefinitions>().load(files.list(Settings["definitions.inventories"]), files.list(Settings["definitions.shops"]))
            }
            "graphic defs", "graphics", "gfx" -> get<GraphicDefinitions>().load(files.list(Settings["definitions.graphics"]))
            "npc defs" -> get<NPCDefinitions>().load(files.list(Settings["definitions.npcs"]))
            "item on item", "item-on-item" -> {
                get<ItemOnItemDefinitions>().load(files.list(Settings["definitions.itemOnItem"]))
            }
            "sound", "sounds", "sound effects" -> get<SoundDefinitions>().load(files.list(Settings["definitions.sounds"]))
            "quest", "quests" -> get<QuestDefinitions>().load(files.find(Settings["definitions.quests"]))
            "midi" -> get<MidiDefinitions>().load(files.list(Settings["definitions.midis"]))
            "vars", "variables" -> get<VariableDefinitions>().load(
                files.list(Settings["definitions.variables.players"]),
                files.list(Settings["definitions.variables.bits"]),
                files.list(Settings["definitions.variables.clients"]),
                files.list(Settings["definitions.variables.strings"]),
                files.list(Settings["definitions.variables.customs"]),
            )
            "music", "music effects", "jingles" -> get<JingleDefinitions>().load(files.list(Settings["definitions.jingles"]))
            "interfaces" -> get<InterfaceDefinitions>().load(files.list(Settings["definitions.interfaces"]), files.find(Settings["definitions.interfaces.types"]))
            "spells" -> get<SpellDefinitions>().load(files.find(Settings["definitions.spells"]))
            "patrols", "paths" -> get<PatrolDefinitions>().load(files.list(Settings["definitions.patrols"]))
            "prayers" -> get<PrayerDefinitions>().load(files.find(Settings["definitions.prayers"]))
            "drops" -> get<DropTables>().load(files.list(Settings["spawns.drops"]))
            "cs2", "cs2s", "client scripts" -> get<ClientScriptDefinitions>().load(files.list(Settings["definitions.clientScripts"]))
            "settings", "setting", "game setting", "game settings", "games settings", "properties", "props" -> {
                Settings.load()
                Publishers.all.publishWorld(World, "settings_reload")
            }
        }
    }

    @Command("shop (shop-id)", description = "open a shop by id", rights = PlayerRights.ADMIN)
    fun shop(player: Player, content: String) {
        player.openShop(content)
    }

    @Command("debug", description = "toggle debug mode and printing logs", rights = PlayerRights.ADMIN)
    fun debug(player: Player, content: String) {
        val target = if (content.isNotEmpty()) {
            players.get(content)
        } else {
            player
        }
        if (target == null) {
            player.message("Unable to find player with name '$content'.")
            return
        }
        target["debug"] = !target["debug", false]
        player.message("Debugging ${if (target["debug", false]) "enabled" else "disabled"} for player '${target.name}'.")
    }

    @Command("chance (drop-table-id)", description = "get the chances for all items of a drop table", rights = PlayerRights.MOD)
    fun chance(player: Player, content: String) {
        val table = tables.get(content) ?: tables.get("${content}_drop_table")
        if (table == null) {
            player.message("No drop table found for '$content'")
            return
        }
        val chances = mutableMapOf<ItemDrop, Double>()
        collectChances(player, table, chances)
        for ((drop, chance) in chances) {
            val amount = when {
                drop.amount.first == drop.amount.last && drop.amount.first > 1 -> "(${drop.amount.first})"
                drop.amount.first != drop.amount.last && drop.amount.first > 1 -> "(${drop.amount.first}-${drop.amount.last})"
                else -> ""
            }
            player.message("${drop.id} $amount - 1/${chance.toInt()}")
        }
    }

    @Command("sim (drop-table-name) (drop-count)", description = "any amount of drops from a drop-table", rights = PlayerRights.MOD)
    fun sim(player: Player, content: String) {
        val parts = content.split(" ")
        val name = parts.first()
        val count = parts.last().toSIInt()
        val table = tables.get(name) ?: tables.get("${name}_drop_table")
        val title = "${count.toSIPrefix()} '${name.removeSuffix("_drop_table")}' drops"
        if (table == null) {
            player.message("No drop table found for '$name'")
            return
        }
        if (count < 0) {
            player.message("Simulation count has to be more than 0.")
            return
        }
        player.message("Simulating $title")
        if (count > 100_000) {
            player.message("Calculating...")
        }
        GlobalScope.launch {
            val inventory = Inventory.debug(capacity = 100, id = "")
            coroutineScope {
                val time = measureTimeMillis {
                    (0 until count).chunked(1_000_000).map { numbers ->
                        async {
                            val temp = Inventory.debug(capacity = 100)
                            val list = InventoryDelegate(temp)
                            for (i in numbers) {
                                table.role(list = list, player = player)
                            }
                            temp
                        }
                    }.forEach {
                        if (!it.await().moveAll(inventory)) {
                            println("Failed to move all simulated drops to inventory")
                        }
                    }
                }
                if (time > 0) {
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(time)
                    player.message("Simulation took ${if (seconds > 1) "${seconds}s" else "${time}ms"}")
                }
            }
            val alch: (Item) -> Long = {
                it.amount * it.def.cost.toLong()
            }
            val exchange: (Item) -> Long = {
                it.amount * it.def["price", it.def.cost].toLong()
            }
            val chances = mutableMapOf<ItemDrop, Double>()
            collectChances(player, table, chances)
            val itemChances = chances.map { it.key.id to it }.toMap()
            val sortByPrice = false
            try {
                if (sortByPrice) {
                    inventory.sortedByDescending { exchange(it) }
                } else {
                    inventory.sortedByDescending { it.amount.toLong() }
                }
                World.queue("drop_sim") {
                    var alchValue = 0L
                    var exchangeValue = 0L
                    for (item in inventory.items) {
                        if (item.isNotEmpty()) {
                            alchValue += alch(item)
                            exchangeValue += exchange(item)
                            val (drop, chance) = itemChances[item.id] ?: continue
                            player.message("${item.id} 1/${(count / (item.amount / drop.amount.first.toDouble())).toInt()} (1/${chance.toInt()} real)")
                        }
                    }
                    player.message("Alch price: ${alchValue.toDigitGroupString()}gp (${alchValue.toSIPrefix()})")
                    player.message("Exchange price: ${exchangeValue.toDigitGroupString()}gp (${exchangeValue.toSIPrefix()})")
                    player.interfaces.open("shop")
                    player["free_inventory"] = -1
                    player["main_inventory"] = 510
                    player.interfaceOptions.unlock("shop", "stock", 0 until inventory.size * 6, "Info")
                    for ((index, item) in inventory.items.withIndex()) {
                        player["amount_$index"] = item.amount
                    }
                    player.sendInventory(inventory, id = 510)
                    player.interfaces.sendVisibility("shop", "store", false)
                    player.interfaces.sendText("shop", "title", "$title - ${alchValue.toDigitGroupString()}gp (${alchValue.toSIPrefix()})")
                }
            } catch (e: Exception) {
                player.close("shop")
            }
        }
    }

    @Command("update (time)[unit]", description = "start a system shutdown after a set amount of time e.g. 'update 100' = 1 minute or 'update 1h 2m 3s'", rights = PlayerRights.ADMIN)
    fun update(player: Player, content: String) {
        var ticks = 0
        val input = content.toIntOrNull()
        if (input == null) {
            if (content.isBlank()) {
                shutdown(0)
                return
            }
            for (part in content.split(" ")) {
                when {
                    part.endsWith("h") -> ticks += TimeUnit.HOURS.toTicks(part.removeSuffix("h").toInt())
                    part.endsWith("m") -> ticks += TimeUnit.MINUTES.toTicks(part.removeSuffix("m").toInt())
                    part.endsWith("s") -> ticks += TimeUnit.SECONDS.toTicks(part.removeSuffix("s").toInt())
                }
            }
            if (ticks == 0) {
                player.message("Unknown input '$content' please use hours minutes or seconds - e.g. 4h 20m 5s.", ChatType.Console)
                return
            }
        } else {
            ticks = input
        }
        if (ticks >= Short.MAX_VALUE) {
            player.message("Update cannot exceed ${Short.MAX_VALUE} ticks (5 hours 26 mins 43 seconds)", ChatType.Console)
            return
        }
        if (ticks < 1) {
            player.message("Update time must be positive.", ChatType.Console)
            return
        }
        for (player in players) {
            player.client?.systemUpdate(ticks)
        }
        shutdown((ticks - 2).coerceAtLeast(0))
    }

    fun <T> search(player: Player, definitions: DefinitionsDecoder<T>, search: String, getName: (T) -> String): Int where T : Definition, T : Extra {
        var found = 0
        for (id in definitions.definitions.indices) {
            val def = definitions.getOrNull(id) ?: continue
            val name = getName(def)
            if (name.lowercase().contains(search) || def.stringId.lowercase().contains(search)) {
                player.message("[${name.lowercase().replace(utf8Regex, "")}] - id: $id${if (def.stringId.isNotBlank()) " (${def.stringId})" else ""}", ChatType.Console)
                found++
            }
        }
        return found
    }

    class InventoryDelegate(
        private val inventory: Inventory,
        private val list: MutableList<ItemDrop> = mutableListOf(),
    ) : MutableList<ItemDrop> by list {
        override fun add(element: ItemDrop): Boolean {
            if (!inventory.add(element.id, element.amount.random()) && element.id != "nothing") {
                println("Failed to add $element")
            }
            return true
        }
    }

    fun ItemDrop.chance(table: DropTable): Double {
        if (table.type == TableType.All) {
            return 1.0
        }
        if (chance <= 0) {
            return 0.0
        }
        return table.roll / chance.toDouble()
    }

    fun collectChances(player: Player, table: DropTable, map: MutableMap<ItemDrop, Double>, multiplier: Double = 1.0) {
        for (drop in table.drops) {
            if (drop is ItemDrop) {
                val chance = drop.chance(table) * multiplier
                map[drop] = chance
            } else if (drop is DropTable) {
                val chance = if (table.type == TableType.First && drop.chance > 0) table.roll / drop.chance.toDouble() else 1.0
                collectChances(player, drop, map, chance)
            }
        }
    }

    fun Inventory.sortedByDescending(block: (Item) -> Long) {
        transaction {
            val items = items.clone()
            clear()
            items.sortedByDescending(block).forEachIndexed { index, item ->
                set(index, item)
            }
        }
    }

    fun shutdown(ticks: Int) {
        // Prevent players logging-in 1 minute before update
        World.queue("system_shutdown", (ticks - 100).coerceAtLeast(0)) {
            accountLoader.update = true
        }
        World.queue("system_update", ticks) {
            Main.server.stop()
        }
    }
}

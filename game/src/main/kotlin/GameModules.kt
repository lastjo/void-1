@file:Suppress("USELESS_CAST")

import com.github.michaelbull.logging.InlineLogger
import content.bot.TaskManager
import content.bot.interact.navigation.graph.NavigationGraph
import content.bot.interact.path.Dijkstra
import content.bot.interact.path.DijkstraFrontier
import content.entity.obj.ObjectTeleports
import content.entity.obj.ship.CharterShips
import content.entity.player.modal.book.Books
import content.entity.world.music.MusicTracks
import content.quest.member.fairy_tale_part_2.fairy_ring.FairyRingCodes
import content.social.trade.exchange.GrandExchange
import content.social.trade.exchange.history.ExchangeHistory
import kotlinx.io.pool.DefaultPool
import org.koin.dsl.module
import org.rsmod.game.pathfinder.LineValidator
import world.gregs.voidps.engine.client.PlayerAccountLoader
import world.gregs.voidps.engine.client.instruction.InstructionHandlers
import world.gregs.voidps.engine.client.instruction.InterfaceHandler
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.data.*
import world.gregs.voidps.engine.data.definition.*
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.item.drop.DropTables
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.item.floor.ItemSpawns
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.map.collision.Collisions
import world.gregs.voidps.engine.map.zone.DynamicZones
import world.gregs.voidps.engine.script.PublishersImpl

fun gameModule(files: ConfigFiles) = module {
    single { ItemSpawns() }
    single { TaskManager() }
    single {
        val size = get<NavigationGraph>().size
        Dijkstra(
            get(),
            object : DefaultPool<DijkstraFrontier>(10) {
                override fun produceInstance() = DijkstraFrontier(size)
            },
        )
    }
    single(createdAtStart = true) { NavigationGraph(get(), get()).load(files.find(Settings["map.navGraph"])) }
    single(createdAtStart = true) { Books().load(files.list(Settings["definitions.books"])) }
    single(createdAtStart = true) { MusicTracks().load(files.find(Settings["map.music"])) }
    single(createdAtStart = true) { FairyRingCodes().load(files.find(Settings["definitions.fairyCodes"])) }
    single(createdAtStart = true) { CharterShips().load(files.find(Settings["map.ships.prices"])) }
    single {
        InstructionHandlers(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            InterfaceHandler(get(), get(), get(), get()),
        )
    }
    single(createdAtStart = true) {
        get<Storage>().offers(Settings["grandExchange.offers.activeDays", 0])
    }
    single(createdAtStart = true) {
        ExchangeHistory(get(), get<Storage>().priceHistory().toMutableMap()).also { it.calculatePrices() }
    }
    single(createdAtStart = true) {
        GrandExchange(get(), get(), get<Storage>().claims().toMutableMap(), get(), get(), get(), get())
    }
    single(createdAtStart = true) {
        val logger = InlineLogger("Publishers")
        val fairyCodes = get<FairyRingCodes>()
        val variableDefinitions = get<VariableDefinitions>()
        val npcs = get<NPCs>()
        val players = get<Players>()
        val objects = get<GameObjects>()
        val floorItems = get<FloorItems>()
        val itemDefinitions = get<ItemDefinitions>()
        val inventoryDefinitions = get<InventoryDefinitions>()
        val areas = get<AreaDefinitions>()
        val styleDefinitions = get<WeaponStyleDefinitions>()
        val enumDefinitions = get<EnumDefinitions>()
        val structDefinitions = get<StructDefinitions>()
        val lineValidator = get<LineValidator>()
        val patrols = get<PatrolDefinitions>()
        val taskManager = get<TaskManager>()
        val playerAccountLoader = get<PlayerAccountLoader>()
        val animationDefinitions = get<AnimationDefinitions>()
        val canoeDefinitions = get<CanoeDefinitions>()
        val npcDefinitions = get<NPCDefinitions>()
        val soundDefinitions = get<SoundDefinitions>()
        val spellDefinitions = get<SpellDefinitions>()
        val itemSpawns = get<ItemSpawns>()
        val charterShips = get<CharterShips>()

        val objectTeleports = get<ObjectTeleports>()
        val grandExchange = get<GrandExchange>()
        val dropTables = get<DropTables>()
        val interfaceDefinitions = get<InterfaceDefinitions>()
        val collisions = get<Collisions>()
        val books = get<Books>()
        val itemOnItemDefinitions = get<ItemOnItemDefinitions>()
        val musicTracks = get<MusicTracks>()
        val accountManager = get<AccountManager>()
        val saveQueue = get<SaveQueue>()
        val questDefinitions = get<QuestDefinitions>()
        val dynamicZones = get<DynamicZones>()
        val fontDefinitions = get<FontDefinitions>()
        val objectDefinitions = get<ObjectDefinitions>()
        val weaponAnimationDefinitions = get<WeaponAnimationDefinitions>()
        val ammoDefinitions = get<AmmoDefinitions>()
        val prayerDefinitions = get<PrayerDefinitions>()
        val slayerTaskDefinitions = get<SlayerTaskDefinitions>()
        val start = System.currentTimeMillis()
        val publishers = PublishersImpl(
            fairyRingCodes = fairyCodes,
            lineValidator = lineValidator,
            areaDefinitions = areas,
            enumDefinitions = enumDefinitions,
            inventoryDefinitions = inventoryDefinitions,
            itemDefinitions = itemDefinitions,
            patrolDefinitions = patrols,
            structDefinitions = structDefinitions,
            variableDefinitions = variableDefinitions,
            weaponStyleDefinitions = styleDefinitions,
            npcs = npcs,
            players = players,
            floorItems = floorItems,
            gameObjects = objects,
            taskManager = taskManager,
            playerAccountLoader = playerAccountLoader,
            animationDefinitions = animationDefinitions,
            canoeDefinitions = canoeDefinitions,
            npcDefinitions = npcDefinitions,
            soundDefinitions = soundDefinitions,
            spellDefinitions = spellDefinitions,
            itemSpawns = itemSpawns,
            charterShips = charterShips,
            objectTeleports = objectTeleports,
            grandExchange = grandExchange,
            dropTables = dropTables,
            interfaceDefinitions = interfaceDefinitions,
            collisions = collisions,
            books = books,
            itemOnItemDefinitions = itemOnItemDefinitions,
            musicTracks = musicTracks,
            accountManager = accountManager,
            saveQueue = saveQueue,
            questDefinitions = questDefinitions,
            dynamicZones = dynamicZones,
            fontDefinitions = fontDefinitions,
            objectDefinitions = objectDefinitions,
            weaponAnimationDefinitions = weaponAnimationDefinitions,
            ammoDefinitions = ammoDefinitions,
            prayerDefinitions = prayerDefinitions,
            slayerTaskDefinitions = slayerTaskDefinitions,
        )
        Publishers.set(publishers)
        logger.info { "Loaded ${publishers.subscriptions} publisher ${"subscriptions".plural(publishers.subscriptions)} in ${System.currentTimeMillis() - start} ms" }
        publishers as Publishers
    }
}

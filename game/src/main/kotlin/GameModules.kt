@file:Suppress("USELESS_CAST")

import com.github.michaelbull.logging.InlineLogger
import content.bot.TaskManager
import content.bot.interact.navigation.graph.NavigationGraph
import content.bot.interact.path.Dijkstra
import content.bot.interact.path.DijkstraFrontier
import content.entity.obj.ship.CharterShips
import content.entity.player.modal.book.Books
import content.entity.world.music.MusicTracks
import content.quest.member.fairy_tale_part_2.fairy_ring.FairyRingCodes
import content.social.trade.exchange.GrandExchange
import content.social.trade.exchange.history.ExchangeHistory
import kotlinx.io.pool.DefaultPool
import org.koin.dsl.module
import world.gregs.voidps.engine.client.instruction.InstructionHandlers
import world.gregs.voidps.engine.client.instruction.InterfaceHandler
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.data.*
import world.gregs.voidps.engine.data.definition.VariableDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.item.floor.ItemSpawns
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.event.Publishers
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
            get(),
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
        val start = System.currentTimeMillis()
        val publishers = PublishersImpl(fairyCodes, variableDefinitions, npcs, players, objects)
        npcs.publishers = publishers
        objects.publishers = publishers
        floorItems.publishers = publishers
        logger.info { "Loaded ${publishers.subscriptions} publisher ${"subscriptions".plural(publishers.subscriptions)} in ${System.currentTimeMillis() - start} ms" }
        publishers as Publishers
    }
}

package world.gregs.voidps.engine.script

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import world.gregs.voidps.engine.script.sub.*

// TODO move this to a separate module
class PublisherProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
        schemas = mapOf(
            Option::class.qualifiedName!! to listOf(
                listOf("Player", "GameObject") to OptionPublisher(OBJECT),
                listOf("Player", "Player") to OptionPublisher(PLAYER),
                listOf("Player", "NPC") to OptionPublisher(NPC),
                listOf("Player", "FloorItem") to OptionPublisher(FLOOR_ITEM),
            ),
            Interface::class.qualifiedName!! to listOf(
                listOf("Player") to InterfacePublisher(),
            ),
            UseOn::class.qualifiedName!! to listOf(
                listOf("Player", "Player") to InterfaceOnPublisher(PLAYER),
                listOf("Player", "NPC") to InterfaceOnPublisher(NPC),
                listOf("Player", "Item") to InterfaceOnPublisher(ITEM),
                listOf("Player", "GameObject") to InterfaceOnPublisher(OBJECT),
                listOf("Player", "FloorItem") to InterfaceOnPublisher(FLOOR_ITEM),
            ),
            Subscribe::class.qualifiedName!! to listOf(
                listOf("Player") to SubscribePublisher(),
            ),
            Spawn::class.qualifiedName!! to listOf(
                listOf("Player") to SpawnPublisher("player", PLAYER),
                listOf("NPC") to SpawnPublisher("npc", NPC),
                listOf("GameObject") to SpawnPublisher("obj", OBJECT),
                listOf("FloorItem") to SpawnPublisher("floorItem", FLOOR_ITEM),
                listOf("World") to SpawnPublisher("world", WORLD),
            ),
            Open::class.qualifiedName!! to listOf(
                listOf("Player") to OpenPublisher(),
            ),
            Close::class.qualifiedName!! to listOf(
                listOf("Player") to ClosePublisher(),
            ),
            TimerStart::class.qualifiedName!! to listOf(
                listOf("Player") to TimerStartPublisher("player", PLAYER),
                listOf("NPC") to TimerStartPublisher("npc", NPC),
            ),
            TimerTick::class.qualifiedName!! to listOf(
                listOf("Player") to TimerTickPublisher("player", PLAYER),
                listOf("NPC") to TimerTickPublisher("npc", NPC),
            ),
            TimerStop::class.qualifiedName!! to listOf(
                listOf("Player") to TimerStopPublisher("player", PLAYER),
                listOf("NPC") to TimerStopPublisher("npc", NPC),
            ),
            Added::class.qualifiedName!! to listOf(
                listOf("Player") to ItemAddedPublisher(),
            ),
            Removed::class.qualifiedName!! to listOf(
                listOf("Player") to ItemRemovedPublisher(),
            ),
            Command::class.qualifiedName!! to listOf(
                listOf("Player") to CommandPublisher(),
            ),
        )
    )
}

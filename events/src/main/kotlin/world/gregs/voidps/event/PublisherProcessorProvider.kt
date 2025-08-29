package world.gregs.voidps.event

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.ClassName
import world.gregs.voidps.event.sub.*
import world.gregs.voidps.type.sub.*
import world.gregs.voidps.type.sub.ItemAdded

/**
 * Register for Kotlin Symbol Processing which provides a [PublisherProcessor]
 *
 * Lists [PublisherProcessor.schemas]:
 *  Annotation -> Required parameters -> [Publisher] schema
 */
class PublisherProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
        superclass = ClassName("world.gregs.voidps.engine.event", "Publishers"),
        schemas = mapOf(
            Option::class.qualifiedName!! to listOf(
                listOf("Player", "GameObject") to OptionPublisher("player", PLAYER, OBJECT),
                listOf("Player", "Player") to OptionPublisher("player", PLAYER, PLAYER),
                listOf("Player", "NPC") to OptionPublisher("player", PLAYER, NPC),
                listOf("Player", "FloorItem") to OptionPublisher("player", PLAYER, FLOOR_ITEM),
                listOf("NPC", "GameObject") to OptionPublisher("npc", NPC, OBJECT),
                listOf("NPC", "Player") to OptionPublisher("npc", NPC, PLAYER),
                listOf("NPC", "NPC") to OptionPublisher("npc", NPC, NPC),
                listOf("NPC", "FloorItem") to OptionPublisher("npc", NPC, FLOOR_ITEM),
                listOf("Character", "GameObject") to OptionPublisher("character", CHARACTER, OBJECT),
                listOf("Character", "Player") to OptionPublisher("character", CHARACTER, PLAYER),
                listOf("Character", "NPC") to OptionPublisher("character", CHARACTER, NPC),
                listOf("Character", "FloorItem") to OptionPublisher("character", CHARACTER, FLOOR_ITEM),
            ),
            Interface::class.qualifiedName!! to listOf(
                listOf("Player") to InterfacePublisher(),
            ),
            UseOn::class.qualifiedName!! to listOf(
                listOf("Player", "Player") to InterfaceOnPublisher(PLAYER),
                listOf("Player", "NPC") to InterfaceOnPublisher(NPC),
                listOf("Player", "Character") to InterfaceOnPublisher(CHARACTER),
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
                listOf("Character") to SpawnPublisher("character", CHARACTER),
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
                listOf("Character") to TimerStartPublisher("character", CHARACTER),
                listOf("World") to TimerStartPublisher("world", WORLD),
            ),
            TimerTick::class.qualifiedName!! to listOf(
                listOf("Player") to TimerTickPublisher("player", PLAYER),
                listOf("NPC") to TimerTickPublisher("npc", NPC),
                listOf("Character") to TimerTickPublisher("character", CHARACTER),
            ),
            TimerStop::class.qualifiedName!! to listOf(
                listOf("Player") to TimerStopPublisher("player", PLAYER),
                listOf("NPC") to TimerStopPublisher("npc", NPC),
                listOf("Character") to TimerStopPublisher("character", CHARACTER),
            ),
            ItemAdded::class.qualifiedName!! to listOf(
                listOf("Player") to ItemAddedPublisher(),
            ),
            ItemRemoved::class.qualifiedName!! to listOf(
                listOf("Player") to ItemRemovedPublisher(),
            ),
            Command::class.qualifiedName!! to listOf(
                listOf("Player") to CommandPublisher(),
            ),
        ),
    )
}

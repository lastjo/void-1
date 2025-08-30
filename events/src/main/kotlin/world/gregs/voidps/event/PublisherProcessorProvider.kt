package world.gregs.voidps.event

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.ClassName
import world.gregs.voidps.engine.event.Publishers
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
                listOf("Player", "GameObject") to OptionPublisher(Publishers::playerGameObjectOption, Publishers::hasPlayerGameObjectOption),
                listOf("Player", "Player") to OptionPublisher(Publishers::playerPlayerOption, Publishers::hasPlayerPlayerOption),
                listOf("Player", "NPC") to OptionPublisher(Publishers::playerNPCOption, Publishers::hasPlayerNPCOption),
                listOf("Player", "FloorItem") to OptionPublisher(Publishers::playerFloorItemOption, Publishers::hasPlayerFloorItemOption),
                listOf("NPC", "GameObject") to OptionPublisher(Publishers::npcGameObjectOption, Publishers::hasNPCGameObjectOption),
                listOf("NPC", "Player") to OptionPublisher(Publishers::npcPlayerOption, Publishers::hasNPCPlayerOption),
                listOf("NPC", "NPC") to OptionPublisher(Publishers::npcNPCOption, Publishers::hasNPCNPCOption),
                listOf("NPC", "FloorItem") to OptionPublisher(Publishers::npcFloorItemOption, Publishers::hasNPCFloorItemOption),
                listOf("Character", "GameObject") to OptionPublisher(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
                listOf("Character", "Player") to OptionPublisher(Publishers::characterPlayerOption, Publishers::hasCharacterPlayerOption),
                listOf("Character", "NPC") to OptionPublisher(Publishers::characterNPCOption, Publishers::hasCharacterNPCOption),
                listOf("Character", "FloorItem") to OptionPublisher(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
            ),
            Interface::class.qualifiedName!! to listOf(
                listOf("Player") to InterfacePublisher(),
            ),
            UseOn::class.qualifiedName!! to listOf(
                listOf("Player", "Player") to InterfaceOnPublisher(Publishers::interfaceOnPlayer, Publishers::hasInterfaceOnPlayer),
                listOf("Player", "NPC") to InterfaceOnPublisher(Publishers::interfaceOnNPC, Publishers::hasInterfaceOnNPC),
                listOf("Player", "Character") to InterfaceOnPublisher(Publishers::interfaceOnCharacter, Publishers::hasInterfaceOnCharacter),
                listOf("Player", "Item") to InterfaceOnPublisher(Publishers::interfaceOnItem, Publishers::hasInterfaceOnItem),
                listOf("Player", "GameObject") to InterfaceOnPublisher(Publishers::interfaceOnGameObject, Publishers::hasInterfaceOnGameObject),
                listOf("Player", "FloorItem") to InterfaceOnPublisher(Publishers::interfaceOnFloorItem, Publishers::hasInterfaceOnFloorItem),
            ),
            Subscribe::class.qualifiedName!! to listOf(
                listOf("Player") to SubscribePublisher(Publishers::publishPlayer),
                listOf("NPC") to SubscribePublisher(Publishers::publishNPC),
                listOf("GameObject") to SubscribePublisher(Publishers::publishGameObject),
                listOf("FloorItem") to SubscribePublisher(Publishers::publishFloorItem),
                listOf("World") to SubscribePublisher(Publishers::publishWorld),
                emptyList<String>() to SubscribePublisher(Publishers::publish),
            ),
            Spawn::class.qualifiedName!! to listOf(
                listOf("Player") to SpawnPublisher(Publishers::spawnPlayer),
                listOf("NPC") to SpawnPublisher(Publishers::spawnNPC),
                listOf("Character") to SpawnPublisher(Publishers::spawnCharacter),
                listOf("GameObject") to SpawnPublisher(Publishers::spawnGameObject),
                listOf("FloorItem") to SpawnPublisher(Publishers::spawnFloorItem),
                listOf("World") to SpawnPublisher(Publishers::spawnWorld),
            ),
            Despawn::class.qualifiedName!! to listOf(
                listOf("Player") to SpawnPublisher(Publishers::despawnPlayer),
                listOf("NPC") to SpawnPublisher(Publishers::despawnNPC),
                listOf("Character") to SpawnPublisher(Publishers::despawnCharacter),
                listOf("GameObject") to SpawnPublisher(Publishers::despawnGameObject),
                listOf("FloorItem") to SpawnPublisher(Publishers::despawnFloorItem),
                listOf("World") to SpawnPublisher(Publishers::despawnWorld),
            ),
            Open::class.qualifiedName!! to listOf(
                listOf("Player") to InterfaceChangePublisher(Publishers::interfaceOpen),
            ),
            Close::class.qualifiedName!! to listOf(
                listOf("Player") to InterfaceChangePublisher(Publishers::interfaceClosed),
            ),
            TimerStart::class.qualifiedName!! to listOf(
                listOf("Player") to TimerPublisher(Publishers::timerStartPlayer),
                listOf("NPC") to TimerPublisher(Publishers::timerStartNPC),
                listOf("Character") to TimerPublisher(Publishers::timerStartCharacter),
                listOf("World") to TimerPublisher(Publishers::timerStartWorld),
            ),
            TimerTick::class.qualifiedName!! to listOf(
                listOf("Player") to TimerPublisher(Publishers::timerTickPlayer),
                listOf("NPC") to TimerPublisher(Publishers::timerTickNPC),
                listOf("Character") to TimerPublisher(Publishers::timerTickCharacter),
                listOf("World") to TimerPublisher(Publishers::timerTickWorld),
            ),
            TimerStop::class.qualifiedName!! to listOf(
                listOf("Player") to TimerPublisher(Publishers::timerStopPlayer),
                listOf("NPC") to TimerPublisher(Publishers::timerStopNPC),
                listOf("Character") to TimerPublisher(Publishers::timerStopCharacter),
                listOf("World") to TimerPublisher(Publishers::timerStopWorld),
            ),
            ItemAdded::class.qualifiedName!! to listOf(
                listOf("Player") to ItemChangePublisher(Publishers::itemAdded),
            ),
            ItemRemoved::class.qualifiedName!! to listOf(
                listOf("Player") to ItemChangePublisher(Publishers::itemRemoved),
            ),
            Command::class.qualifiedName!! to listOf(
                listOf("Player") to CommandPublisher(),
            ),
        ),
    )
}

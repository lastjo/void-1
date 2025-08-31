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
 *  Annotation -> [Publisher] schema
 */
class PublisherProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
        superclass = ClassName("world.gregs.voidps.engine.event", "Publishers"),
        schemas = mapOf(
            Option::class.qualifiedName!! to listOf(
                OptionPublisher(Publishers::playerGameObjectOption, Publishers::hasPlayerGameObjectOption),
                OptionPublisher(Publishers::playerPlayerOption, Publishers::hasPlayerPlayerOption),
                OptionPublisher(Publishers::playerNPCOption, Publishers::hasPlayerNPCOption),
                OptionPublisher(Publishers::playerFloorItemOption, Publishers::hasPlayerFloorItemOption),
                OptionPublisher(Publishers::npcGameObjectOption, Publishers::hasNPCGameObjectOption),
                OptionPublisher(Publishers::npcPlayerOption, Publishers::hasNPCPlayerOption),
                OptionPublisher(Publishers::npcNPCOption, Publishers::hasNPCNPCOption),
                OptionPublisher(Publishers::npcFloorItemOption, Publishers::hasNPCFloorItemOption),
                OptionPublisher(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
                OptionPublisher(Publishers::characterPlayerOption, Publishers::hasCharacterPlayerOption),
                OptionPublisher(Publishers::characterNPCOption, Publishers::hasCharacterNPCOption),
                OptionPublisher(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
            ),
            Interface::class.qualifiedName!! to listOf(InterfacePublisher()),
            Inventory::class.qualifiedName!! to listOf(InventoryPublisher()),
            UseOn::class.qualifiedName!! to listOf(
                InterfaceOnPublisher(Publishers::interfaceOnPlayer, Publishers::hasInterfaceOnPlayer),
                InterfaceOnPublisher(Publishers::interfaceOnNPC, Publishers::hasInterfaceOnNPC),
                InterfaceOnPublisher(Publishers::interfaceOnCharacter, Publishers::hasInterfaceOnCharacter),
                InterfaceOnPublisher(Publishers::interfaceOnGameObject, Publishers::hasInterfaceOnGameObject),
                InterfaceOnPublisher(Publishers::interfaceOnItem, Publishers::hasInterfaceOnItem),
                InterfaceOnPublisher(Publishers::interfaceOnFloorItem, Publishers::hasInterfaceOnFloorItem),
            ),
            Subscribe::class.qualifiedName!! to listOf(
                SubscribePublisher(Publishers::publishPlayer),
                SubscribePublisher(Publishers::publishNPC),
                SubscribePublisher(Publishers::publishGameObject),
                SubscribePublisher(Publishers::publishFloorItem),
                SubscribePublisher(Publishers::publishWorld),
                SubscribePublisher(Publishers::publish),
            ),
            Spawn::class.qualifiedName!! to listOf(
                SpawnPublisher(Publishers::spawnPlayer),
                SpawnPublisher(Publishers::spawnNPC),
                SpawnPublisher(Publishers::spawnCharacter),
                SpawnPublisher(Publishers::spawnGameObject),
                SpawnPublisher(Publishers::spawnFloorItem),
                SpawnPublisher(Publishers::spawnWorld),
            ),
            Despawn::class.qualifiedName!! to listOf(
                SpawnPublisher(Publishers::despawnPlayer),
                SpawnPublisher(Publishers::despawnNPC),
                SpawnPublisher(Publishers::despawnCharacter),
                SpawnPublisher(Publishers::despawnGameObject),
                SpawnPublisher(Publishers::despawnFloorItem),
                SpawnPublisher(Publishers::despawnWorld),
            ),
            Death::class.qualifiedName!! to listOf(
                SpawnPublisher(Publishers::playerDeath),
                SpawnPublisher(Publishers::npcDeath),
                SpawnPublisher(Publishers::characterDeath),
            ),
            Take::class.qualifiedName!! to listOf(
                ItemTakePublisher(Publishers::playerTakeItem),
                ItemTakePublisher(Publishers::npcTakeItem),
            ),
            Open::class.qualifiedName!! to listOf(
                InterfaceChangePublisher(Publishers::interfaceOpened),
            ),
            Close::class.qualifiedName!! to listOf(
                InterfaceChangePublisher(Publishers::interfaceClosed),
            ),
            Refresh::class.qualifiedName!! to listOf(
                InterfaceChangePublisher(Publishers::interfaceRefreshed),
            ),
            TimerStart::class.qualifiedName!! to listOf(
                TimerPublisher(Publishers::timerStartPlayer),
                TimerPublisher(Publishers::timerStartNPC),
                TimerPublisher(Publishers::timerStartCharacter),
                TimerPublisher(Publishers::timerStartWorld),
            ),
            TimerTick::class.qualifiedName!! to listOf(
                TimerPublisher(Publishers::timerTickPlayer),
                TimerPublisher(Publishers::timerTickNPC),
                TimerPublisher(Publishers::timerTickCharacter),
                TimerPublisher(Publishers::timerTickWorld),
            ),
            TimerStop::class.qualifiedName!! to listOf(
                TimerPublisher(Publishers::timerStopPlayer),
                TimerPublisher(Publishers::timerStopNPC),
                TimerPublisher(Publishers::timerStopCharacter),
                TimerPublisher(Publishers::timerStopWorld),
            ),
            ItemAdded::class.qualifiedName!! to listOf(ItemChangePublisher(Publishers::itemAdded)),
            ItemRemoved::class.qualifiedName!! to listOf(ItemChangePublisher(Publishers::itemRemoved)),
            InventoryChanged::class.qualifiedName!! to listOf(InventoryChangePublisher(Publishers::inventoryChanged)),
            Command::class.qualifiedName!! to listOf(CommandPublisher()),
            Teleport::class.qualifiedName!! to listOf(TeleportPublisher()),
            Enter::class.qualifiedName!! to listOf(AreaPublisher(Publishers::enterArea)),
            Exit::class.qualifiedName!! to listOf(AreaPublisher(Publishers::exitArea)),
            Move::class.qualifiedName!! to listOf(
                MovePublisher(Publishers::movePlayer),
                MovePublisher(Publishers::moveNPC),
                MovePublisher(Publishers::moveCharacter),
            ),
            LevelChange::class.qualifiedName!! to listOf(
                LevelChangePublisher(Publishers::levelChangePlayer),
                LevelChangePublisher(Publishers::levelChangeNPC),
                LevelChangePublisher(Publishers::levelChangeCharacter),
            ),
            Variable::class.qualifiedName!! to listOf(
                VariableSetPublisher(Publishers::variableSetPlayer),
                VariableSetPublisher(Publishers::variableSetNPC),
                VariableSetPublisher(Publishers::variableSetCharacter),
            ),
            Combat::class.qualifiedName!! to listOf(
                CombatPublisher(Publishers::playerCombatAttackPlayer),
                CombatPublisher(Publishers::playerCombatAttackNPC),
                CombatPublisher(Publishers::playerCombatAttackCharacter),
                CombatPublisher(Publishers::npcCombatAttackPlayer),
                CombatPublisher(Publishers::npcCombatAttackNPC),
                CombatPublisher(Publishers::npcCombatAttackCharacter),
                CombatPublisher(Publishers::characterCombatAttackPlayer),
                CombatPublisher(Publishers::characterCombatAttackNPC),
                CombatPublisher(Publishers::characterCombatAttackCharacter),
            ),
            PrayerStart::class.qualifiedName!! to listOf(
                PrayerPublisher(Publishers::prayerStartPlayer),
                PrayerPublisher(Publishers::prayerStartNPC),
                PrayerPublisher(Publishers::prayerStartCharacter),
            ),
            PrayerStop::class.qualifiedName!! to listOf(
                PrayerPublisher(Publishers::prayerStopPlayer),
                PrayerPublisher(Publishers::prayerStopNPC),
                PrayerPublisher(Publishers::prayerStopCharacter),
            ),
            Consume::class.qualifiedName!! to listOf(ConsumePublisher(Publishers::consume))
        ),
    )
}

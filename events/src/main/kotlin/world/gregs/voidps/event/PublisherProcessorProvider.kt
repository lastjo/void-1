package world.gregs.voidps.event

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.ClassName
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.event.map.*
import world.gregs.voidps.type.sub.*
import world.gregs.voidps.type.sub.ItemAdded

/**
 * Register for Kotlin Symbol Processing which provides a [PublisherProcessor]
 *
 * Lists [PublisherProcessor.schemas]:
 *  Annotation -> [PublisherMapping] schema
 */
class PublisherProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = PublisherProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
        superclass = ClassName("world.gregs.voidps.engine.event", "Publishers"),
        schemas = mapOf(
            Option::class.qualifiedName!! to listOf(
                OptionPublisherMapping(Publishers::playerGameObjectOption, Publishers::hasPlayerGameObjectOption),
                OptionPublisherMapping(Publishers::playerPlayerOption, Publishers::hasPlayerPlayerOption),
                OptionPublisherMapping(Publishers::playerNPCOption, Publishers::hasPlayerNPCOption),
                OptionPublisherMapping(Publishers::playerFloorItemOption, Publishers::hasPlayerFloorItemOption),
                OptionPublisherMapping(Publishers::npcGameObjectOption, Publishers::hasNPCGameObjectOption),
                OptionPublisherMapping(Publishers::npcPlayerOption, Publishers::hasNPCPlayerOption),
                OptionPublisherMapping(Publishers::npcNPCOption, Publishers::hasNPCNPCOption),
                OptionPublisherMapping(Publishers::npcFloorItemOption, Publishers::hasNPCFloorItemOption),
                OptionPublisherMapping(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
                OptionPublisherMapping(Publishers::characterPlayerOption, Publishers::hasCharacterPlayerOption),
                OptionPublisherMapping(Publishers::characterNPCOption, Publishers::hasCharacterNPCOption),
                OptionPublisherMapping(Publishers::characterGameObjectOption, Publishers::hasCharacterGameObjectOption),
            ),
            Interface::class.qualifiedName!! to listOf(InterfacePublisherMapping(Publishers::interfaceOption)),
            Continue::class.qualifiedName!! to listOf(
                InterfacePublisherMapping(Publishers::continueDialogueItem),
                InterfacePublisherMapping(Publishers::continueDialogue),
            ),
            Inventory::class.qualifiedName!! to listOf(InventoryPublisherMapping()),
            UseOn::class.qualifiedName!! to listOf(
                InterfaceOnPublisherMapping(Publishers::interfaceOnPlayer),
                InterfaceOnPublisherMapping(Publishers::interfaceOnNPC),
                InterfaceOnPublisherMapping(Publishers::interfaceOnCharacter),
                InterfaceOnPublisherMapping(Publishers::interfaceOnGameObject),
                InterfaceOnPublisherMapping(Publishers::interfaceOnItem),
                InterfaceOnPublisherMapping(Publishers::interfaceOnFloorItem),
            ),
            Subscribe::class.qualifiedName!! to listOf(
                SubscribePublisherMapping(Publishers::publishPlayer),
                SubscribePublisherMapping(Publishers::publishNPC),
                SubscribePublisherMapping(Publishers::publishGameObject),
                SubscribePublisherMapping(Publishers::publishFloorItem),
                SubscribePublisherMapping(Publishers::publishWorld),
                SubscribePublisherMapping(Publishers::publish),
            ),
            Spawn::class.qualifiedName!! to listOf(
                SpawnPublisherMapping(Publishers::spawnPlayer),
                SpawnPublisherMapping(Publishers::spawnNPC),
                SpawnPublisherMapping(Publishers::spawnCharacter),
                SpawnPublisherMapping(Publishers::spawnGameObject),
                SpawnPublisherMapping(Publishers::spawnFloorItem),
                SpawnPublisherMapping(Publishers::spawnWorld),
            ),
            Despawn::class.qualifiedName!! to listOf(
                SpawnPublisherMapping(Publishers::despawnPlayer),
                SpawnPublisherMapping(Publishers::despawnNPC),
                SpawnPublisherMapping(Publishers::despawnCharacter),
                SpawnPublisherMapping(Publishers::despawnGameObject),
                SpawnPublisherMapping(Publishers::despawnFloorItem),
                SpawnPublisherMapping(Publishers::despawnWorld),
            ),
            Death::class.qualifiedName!! to listOf(
                SpawnPublisherMapping(Publishers::playerDeath),
                SpawnPublisherMapping(Publishers::npcDeath),
                SpawnPublisherMapping(Publishers::characterDeath),
            ),
            Take::class.qualifiedName!! to listOf(
                ItemTakePublisherMapping(Publishers::playerTakeItem),
                ItemTakePublisherMapping(Publishers::npcTakeItem),
            ),
            Open::class.qualifiedName!! to listOf(
                InterfaceChangePublisherMapping(Publishers::interfaceOpened),
            ),
            Close::class.qualifiedName!! to listOf(
                InterfaceChangePublisherMapping(Publishers::interfaceClosed),
            ),
            Refresh::class.qualifiedName!! to listOf(
                InterfaceChangePublisherMapping(Publishers::interfaceRefreshed),
            ),
            TimerStart::class.qualifiedName!! to listOf(
                TimerPublisherMapping(Publishers::timerStartPlayer),
                TimerPublisherMapping(Publishers::timerStartNPC),
                TimerPublisherMapping(Publishers::timerStartCharacter),
                TimerPublisherMapping(Publishers::timerStartWorld),
            ),
            TimerTick::class.qualifiedName!! to listOf(
                TimerPublisherMapping(Publishers::timerTickPlayer),
                TimerPublisherMapping(Publishers::timerTickNPC),
                TimerPublisherMapping(Publishers::timerTickCharacter),
                TimerPublisherMapping(Publishers::timerTickWorld),
            ),
            TimerStop::class.qualifiedName!! to listOf(
                TimerPublisherMapping(Publishers::timerStopPlayer),
                TimerPublisherMapping(Publishers::timerStopNPC),
                TimerPublisherMapping(Publishers::timerStopCharacter),
                TimerPublisherMapping(Publishers::timerStopWorld),
            ),
            ItemAdded::class.qualifiedName!! to listOf(ItemChangePublisherMapping(Publishers::itemAdded)),
            ItemRemoved::class.qualifiedName!! to listOf(ItemChangePublisherMapping(Publishers::itemRemoved)),
            InventorySlotChanged::class.qualifiedName!! to listOf(InventoryChangePublisherMapping(Publishers::inventoryChanged)),
            InventoryUpdated::class.qualifiedName!! to listOf(InventoryChangePublisherMapping(Publishers::inventoryUpdated)),
            Swap::class.qualifiedName!! to listOf(InventorySwapPublisherMapping(Publishers::inventorySwap)),
            Command::class.qualifiedName!! to listOf(CommandPublisherMapping()),
            Teleport::class.qualifiedName!! to listOf(
                TeleportPublisherMapping(Publishers::teleportGameObject, notification = false),
                TeleportPublisherMapping(Publishers::teleport, notification = false),
            ),
            TeleportLand::class.qualifiedName!! to listOf(
                TeleportPublisherMapping(Publishers::teleportLandGameObject, notification = true),
                TeleportPublisherMapping(Publishers::teleportLand, notification = true),
            ),
            Enter::class.qualifiedName!! to listOf(AreaPublisherMapping(Publishers::enterArea)),
            Exit::class.qualifiedName!! to listOf(AreaPublisherMapping(Publishers::exitArea)),
            Move::class.qualifiedName!! to listOf(
                MovePublisherMapping(Publishers::movePlayer),
                MovePublisherMapping(Publishers::moveNPC),
                MovePublisherMapping(Publishers::moveCharacter),
            ),
            LevelChange::class.qualifiedName!! to listOf(
                LevelChangePublisherMapping(Publishers::levelChangePlayer),
                LevelChangePublisherMapping(Publishers::levelChangeNPC),
                LevelChangePublisherMapping(Publishers::levelChangeCharacter),
            ),
            Experience::class.qualifiedName!! to listOf(ExperiencePublisherMapping(Publishers::experience)),
            Variable::class.qualifiedName!! to listOf(
                VariableSetPublisherMapping(Publishers::variableSetPlayer),
                VariableSetPublisherMapping(Publishers::variableSetNPC),
                VariableSetPublisherMapping(Publishers::variableSetCharacter),
            ),
            VariableBits::class.qualifiedName!! to listOf(
                VariableBitsPublisherMapping(Publishers::variableBitsPlayer),
                VariableBitsPublisherMapping(Publishers::variableBitsNPC),
                VariableBitsPublisherMapping(Publishers::variableBitsCharacter),
            ),
            Combat::class.qualifiedName!! to listOf(
                CombatPublisherMapping(Publishers::playerCombatAttackPlayer),
                CombatPublisherMapping(Publishers::playerCombatAttackNPC),
                CombatPublisherMapping(Publishers::playerCombatAttackCharacter),
                CombatPublisherMapping(Publishers::npcCombatAttackPlayer),
                CombatPublisherMapping(Publishers::npcCombatAttackNPC),
                CombatPublisherMapping(Publishers::npcCombatAttackCharacter),
                CombatPublisherMapping(Publishers::characterCombatAttackPlayer),
                CombatPublisherMapping(Publishers::characterCombatAttackNPC),
                CombatPublisherMapping(Publishers::characterCombatAttackCharacter),
            ),
            SpecialAttack::class.qualifiedName!! to listOf(
                SpecialAttackPublisherMapping(Publishers::specialAttack),
                SpecialAttackPublisherMapping(Publishers::specialAttackPrepare),
            ),
            PrayerStart::class.qualifiedName!! to listOf(
                PrayerPublisherMapping(Publishers::prayerStartPlayer),
                PrayerPublisherMapping(Publishers::prayerStartNPC),
                PrayerPublisherMapping(Publishers::prayerStartCharacter),
            ),
            PrayerStop::class.qualifiedName!! to listOf(
                PrayerPublisherMapping(Publishers::prayerStopPlayer),
                PrayerPublisherMapping(Publishers::prayerStopNPC),
                PrayerPublisherMapping(Publishers::prayerStopCharacter),
            ),
            Consume::class.qualifiedName!! to listOf(ConsumePublisherMapping(Publishers::consume)),
            Hunt::class.qualifiedName!! to listOf(
                HuntPublisherMapping(Publishers::huntPlayer),
                HuntPublisherMapping(Publishers::huntNpc),
                HuntPublisherMapping(Publishers::huntFloorItem),
                HuntPublisherMapping(Publishers::huntGameObject),
            ),
        ),
    )
}

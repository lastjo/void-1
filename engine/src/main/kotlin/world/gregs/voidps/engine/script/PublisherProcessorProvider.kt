package world.gregs.voidps.engine.script

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

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
            ),
        )
    )
}

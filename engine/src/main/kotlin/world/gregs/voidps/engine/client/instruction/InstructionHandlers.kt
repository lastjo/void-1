package world.gregs.voidps.engine.client.instruction

import world.gregs.voidps.engine.client.instruction.handle.*
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.NPCDefinitions
import world.gregs.voidps.engine.data.definition.ObjectDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.event.Event
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.get
import world.gregs.voidps.network.client.Instruction
import world.gregs.voidps.network.client.instruction.*

class InstructionHandlers(
    players: Players,
    npcs: NPCs,
    items: FloorItems,
    objects: GameObjects,
    objectDefinitions: ObjectDefinitions,
    npcDefinitions: NPCDefinitions,
    itemDefinitions: ItemDefinitions,
    interfaceDefinitions: InterfaceDefinitions,
    handler: InterfaceHandler,
) {
    private val interactFloorItem = FloorItemOptionHandler(items)
    private val interactDialogue = DialogueContinueHandler(interfaceDefinitions)
    private val interactDialogueItem = DialogueItemContinueHandler(itemDefinitions)
    private val closeInterface = InterfaceClosedHandler()
    val interactInterface = InterfaceOptionHandler(handler, interfaceDefinitions)
    private val moveInventoryItem = InterfaceSwitchHandler(handler)
    private val interactNPC = NPCOptionHandler(npcs, npcDefinitions)
    private val interactObject = ObjectOptionHandler(objects, objectDefinitions)
    private val interactPlayer = PlayerOptionHandler(players)
    private val interactInterfaceNPC = InterfaceOnNPCOptionHandler(npcs, handler)
    private val interactInterfaceObject = InterfaceOnObjectOptionHandler(objects, handler)
    private val interactInterfacePlayer = InterfaceOnPlayerOptionHandler(players, handler)
    val interactInterfaceItem = InterfaceOnInterfaceOptionHandler(handler)
    private val interactInterfaceFloorItem = InterfaceOnFloorItemOptionHandler(items, handler)
    private val executeCommand = ExecuteCommandHandler()

    fun handle(player: Player, instruction: Instruction) {
        when (instruction) {
            is Event -> player.emit(instruction)
            is InteractInterfaceItem -> interactInterfaceItem.validate(player, instruction)
            is InteractInterfacePlayer -> interactInterfacePlayer.validate(player, instruction)
            is InteractInterfaceObject -> interactInterfaceObject.validate(player, instruction)
            is InteractInterfaceNPC -> interactInterfaceNPC.validate(player, instruction)
            is InteractInterfaceFloorItem -> interactInterfaceFloorItem.validate(player, instruction)
            is InteractFloorItem -> interactFloorItem.validate(player, instruction)
            is InteractDialogue -> interactDialogue.validate(player, instruction)
            is InteractDialogueItem -> interactDialogueItem.validate(player, instruction)
            is InterfaceClosedInstruction -> closeInterface.validate(player, instruction)
            is InteractInterface -> interactInterface.validate(player, instruction)
            is MoveInventoryItem -> moveInventoryItem.validate(player, instruction)
            is InteractNPC -> interactNPC.validate(player, instruction)
            is InteractObject -> interactObject.validate(player, instruction)
            is InteractPlayer -> interactPlayer.validate(player, instruction)
            is ExecuteCommand -> executeCommand.validate(player, instruction)
            else -> Publishers.all.instruction(player, instruction)
        }
    }
}

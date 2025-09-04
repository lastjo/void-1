package content.entity

import world.gregs.voidps.cache.definition.data.NPCDefinition
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.NPCDefinitions
import world.gregs.voidps.engine.data.definition.ObjectDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.ChatType
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.network.client.instruction.ExamineItem
import world.gregs.voidps.network.client.instruction.ExamineNpc
import world.gregs.voidps.network.client.instruction.ExamineObject
import world.gregs.voidps.type.sub.Instruction
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.Option

class Examines(
    private val itemDefinitions: ItemDefinitions,
    private val npcDefinitions: NPCDefinitions,
    private val objectDefinitions: ObjectDefinitions,
) {

    @Instruction(ExamineItem::class)
    fun item(player: Player, instruction: ExamineItem) {
        val definition = itemDefinitions.get(instruction.itemId)
        if (definition.contains("examine")) {
            player.message(definition["examine"], ChatType.Game)
        }
    }

    @Instruction(ExamineNpc::class)
    fun npc(player: Player, instruction: ExamineNpc) {
        val definition = npcDefinitions.get(instruction.npcId)
        if (definition.contains("examine")) {
            player.message(definition["examine"], ChatType.Game)
        }
    }

    @Instruction(ExamineObject::class)
    fun obj(player: Player, instruction: ExamineObject) {
        val definition = objectDefinitions.get(instruction.objectId)
        if (definition.contains("examine")) {
            player.message(definition["examine"], ChatType.Game)
        }
    }

    @Interface("Examine", id = "equipment_bonuses")
    fun equipment(player: Player, item: Item) {
        player.message(item.def.getOrNull("examine") ?: return, ChatType.ItemExamine)
    }

    @Inventory("Examine", inventory = "*")
    fun item(player: Player, item: Item) {
        player.message(item.def.getOrNull("examine") ?: return, ChatType.ItemExamine)
    }

    @Option("Examine", approach = true)
    fun operate(player: Player, target: GameObject, def: ObjectDefinition) {
        player.message(def.getOrNull("examine") ?: return, ChatType.ObjectExamine)
    }

    @Option("Examine")
    fun operate(player: Player, target: NPC, def: NPCDefinition) {
        player.message(def.getOrNull("examine") ?: return, ChatType.NPCExamine)
    }
}

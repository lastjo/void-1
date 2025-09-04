package content.entity.player.modal

import com.github.michaelbull.logging.InlineLogger
import world.gregs.voidps.cache.definition.data.InterfaceComponentDefinition
import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.data.definition.InterfaceDefinitions
import world.gregs.voidps.engine.data.definition.InventoryDefinitions
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.entity.character.mode.interact.Interact
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.Players
import world.gregs.voidps.engine.entity.character.player.chat.noInterest
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.event.Publishers
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.network.client.instruction.*
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Instruction

/**
 * Validates interface interactions
 */
class InterfaceClicks(
    private val items: FloorItems,
    private val npcs: NPCs,
    private val objects: GameObjects,
    private val players: Players,
    private val interfaceDefinitions: InterfaceDefinitions,
    private val itemDefinitions: ItemDefinitions,
    private val inventoryDefinitions: InventoryDefinitions,
    private val enumDefinitions: EnumDefinitions,
) {

    private val logger = InlineLogger("InterfaceClicks")

    @Instruction(InterfaceClosedInstruction::class)
    fun interfaceClosed(player: Player, instruction: InterfaceClosedInstruction) {
        val id = player.interfaces.get("main_screen") ?: player.interfaces.get("wide_screen") ?: player.interfaces.get("underlay")
        if (id != null) {
            player.interfaces.close(id)
        }
    }

    private fun getInventoryItem(player: Player, id: String, componentDefinition: InterfaceComponentDefinition, inventoryId: String, item: Int, itemSlot: Int): Item? {
        val itemId = if (item == -1 || item > itemDefinitions.size) "" else itemDefinitions.get(item).stringId
        val slot = when {
            itemSlot == -1 && inventoryId == "worn_equipment" -> player.equipment.indexOf(itemId)
            itemSlot == -1 && inventoryId == "item_loan" -> 0
            itemSlot == -1 && inventoryId == "returned_lent_items" -> 0
            id == "price_checker" -> itemSlot / 2
            id == "shop" -> itemSlot / 6
            id == "grand_exchange" -> componentDefinition.stringId.removePrefix("collect_slot_").toInt()
            else -> itemSlot
        }
        val definition = inventoryDefinitions.get(inventoryId)
        val secondary = !componentDefinition["primary", true]
        val inventory = player.inventories.getOrNull(definition, secondary = secondary)
        if (inventory == null) {
            logger.info { "Player invalid interface inventory [$player, interface=$id, inv=$inventoryId]" }
            return null
        }
        if (slot !in inventory.items.indices) {
            logger.info { "Player interface inventory out of bounds [$player, slot=$slot, inventory=$inventoryId, item_index=$itemSlot, inventory_size=${definition.length}, indicies=${inventory.items.indices}]" }
            return null
        }

        if (!inventory.inBounds(slot) || inventory[slot].id != itemId) {
            logger.info { "Player invalid interface item [$player, interface=$id, inv=$inventoryId, index=$slot, expected_item=$itemId, actual_item=${inventory[slot]}]" }
            return null
        }
        return inventory[slot]
    }

    @Instruction(InteractInterface::class)
    fun option(player: Player, instruction: InteractInterface) {
        val (interfaceId, componentId, itemId, itemSlot, option) = instruction
        var (id, component, item, inventory, options) = getInterfaceItem(player, interfaceId, componentId, itemId, itemSlot) ?: return
        if (options == null) {
            options = interfaceDefinitions.getComponent(id, component)?.get("options") ?: emptyArray()
        }
        if (option !in options.indices) {
            logger.info { "Interface option not found [$player, interface=$interfaceId, component=$componentId, option=$option, options=${options.toList()}]" }
            return
        }
        val selectedOption = options.getOrNull(option) ?: ""
        Publishers.launch {
            Publishers.all.interfaceOption(
                player = player,
                id = id,
                component = component,
                optionIndex = option,
                option = selectedOption,
                item = item,
                itemSlot = itemSlot,
                inventory = inventory,
            )
        }
    }

    @Instruction(MoveInventoryItem::class)
    fun switch(player: Player, instruction: MoveInventoryItem) {
        var (fromInterfaceId, fromComponentId, fromItemId, fromSlot, toInterfaceId, toComponentId, toItemId, toSlot) = instruction
        if (toInterfaceId == 149) {
            toSlot -= 28
            val temp = fromItemId
            fromItemId = toItemId
            toItemId = temp
        }
        val (fromId, fromComponent, fromItem, fromInventory) = getInterfaceItem(player, fromInterfaceId, fromComponentId, fromItemId, fromSlot) ?: return
        val (toId, toComponent, toItem, toInventory) = getInterfaceItem(player, toInterfaceId, toComponentId, toItemId, toSlot) ?: return
        Publishers.all.inventorySwap(player, id = fromId, component = fromComponent, fromItem = fromItem, fromSlot = fromSlot, fromInventory = fromInventory, toId = toId, toComponent = toComponent, toItem = toItem, toSlot = toSlot, toInventory = toInventory)
    }

    @Instruction(InteractInterfacePlayer::class)
    fun onPlayer(player: Player, instruction: InteractInterfacePlayer) {
        val (playerIndex, interfaceId, componentId, itemId, itemSlot) = instruction
        val target = players.indexed(playerIndex) ?: return

        val (id, component, item, inventory) = getInterfaceItem(player, interfaceId, componentId, itemId, itemSlot) ?: return
        player.closeInterfaces()
        val block: suspend (Boolean) -> Unit = { Publishers.all.interfaceOnPlayer(player, target, id, component, item, itemSlot, inventory, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasInterfaceOnPlayer(player, target, id, component, item, itemSlot, inventory, it) }
        player.mode = Interact(player, target, interact = block, has = check)
    }

    @Instruction(InteractInterfaceObject::class)
    fun onObj(player: Player, instruction: InteractInterfaceObject) {
        val (objectId, x, y, interfaceId, componentId, itemId, itemSlot) = instruction
        val tile = Tile(x, y, player.tile.level)
        val obj = objects[tile, objectId]
        if (obj == null) {
            player.noInterest()
            return
        }
        val (id, component, item, inventory) = getInterfaceItem(player, interfaceId, componentId, itemId, itemSlot) ?: return
        player.closeInterfaces()
        val def = obj.def(player)
        val block: suspend (Boolean) -> Unit = { Publishers.all.interfaceOnGameObject(player, obj, def, id, component, item, itemSlot, inventory, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasInterfaceOnGameObject(player, obj, def, id, component, item, itemSlot, inventory, it) }
        player.mode = Interact(player, obj, interact = block, has = check)
    }

    @Instruction(InteractInterfaceNPC::class)
    fun onNPC(player: Player, instruction: InteractInterfaceNPC) {
        val (npcIndex, interfaceId, componentId, itemId, itemSlot) = instruction
        val npc = npcs.indexed(npcIndex) ?: return
        val (id, component, item, inventory) = getInterfaceItem(player, interfaceId, componentId, itemId, itemSlot) ?: return
        player.closeInterfaces()
        player.talkWith(npc)
        val def = npc.def(player)
        val block: suspend (Boolean) -> Unit = { Publishers.all.interfaceOnNPC(player, npc, def, id, component, item, itemSlot, inventory, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasInterfaceOnNPC(player, npc, def, id, component, item, itemSlot, inventory, it) }
        player.mode = Interact(player, npc, interact = block, has = check)
    }

    @Instruction(InteractInterfaceItem::class)
    fun onItem(player: Player, instruction: InteractInterfaceItem) {
        val (fromItemId, toItemId, fromSlot, toSlot, fromInterfaceId, fromComponentId, toInterfaceId, toComponentId) = instruction
        val (fromId, fromComponent, fromItem, fromInventory) = getInterfaceItem(player, fromInterfaceId, fromComponentId, fromItemId, fromSlot) ?: return
        val (_, _, toItem, toInventory) = getInterfaceItem(player, toInterfaceId, toComponentId, toItemId, toSlot) ?: return
        player.closeInterfaces()
        player.queue.clearWeak()
        player.suspension = null
        Publishers.all.interfaceOnItem(player, toItem, fromId, fromComponent, toSlot, fromItem, fromSlot, fromInventory, toInventory)
    }

    @Instruction(InteractInterfaceFloorItem::class)
    fun onFloorItem(player: Player, instruction: InteractInterfaceFloorItem) {
        val (floorItemId, x, y, interfaceId, componentId, itemId, itemSlot) = instruction
        val tile = player.tile.copy(x, y)
        val floorItem = items[tile].firstOrNull { it.def.id == floorItemId }
        if (floorItem == null) {
            logger.warn { "Invalid floor item $itemId $tile" }
            return
        }
        val (id, component, item, inventory) = getInterfaceItem(player, interfaceId, componentId, itemId, itemSlot) ?: return
        player.closeInterfaces()
        val block: suspend (Boolean) -> Unit = { Publishers.all.interfaceOnFloorItem(player, floorItem, id, component, item, itemSlot, inventory, it) }
        val check: (Boolean) -> Boolean = { Publishers.all.hasInterfaceOnFloorItem(player, floorItem, id, component, item, itemSlot, inventory, it) }
        player.mode = Interact(player, floorItem, approachRange = -1, interact = block, has = check)
    }

    private fun getInterfaceItem(player: Player, interfaceId: Int, componentId: Int, itemId: Int, itemSlot: Int): InterfaceData? {
        val id = getOpenInterface(player, interfaceId) ?: return null
        val componentDefinition = getComponentDefinition(player, interfaceId, componentId) ?: return null
        val component = componentDefinition.stringId
        var item = Item.EMPTY
        var inventory = ""
        if (itemId != -1) {
            when {
                id.startsWith("summoning_") && id.endsWith("_creation") -> item = Item(itemDefinitions.get(itemId).stringId)
                id == "summoning_trade_in" -> item = Item(itemDefinitions.get(itemId).stringId)
                id == "exchange_item_sets" -> {
                    val expected = enumDefinitions.get("exchange_item_sets").getInt(itemSlot + 1)
                    if (expected != itemId) {
                        logger.info { "Exchange item sets don't match [$player, expected=$expected, actual=$itemId]" }
                        return null
                    }
                    item = Item(itemDefinitions.get(expected).stringId)
                }
                id == "common_item_costs" -> item = Item(itemDefinitions.get(itemId).stringId)
                else -> {
                    inventory = getInventory(player, id, component, componentDefinition) ?: return null
                    item = getInventoryItem(player, id, componentDefinition, inventory, itemId, itemSlot) ?: return null
                }
            }
        }
        return InterfaceData(id, component, item, inventory, componentDefinition.options)
    }

    private fun getOpenInterface(player: Player, interfaceId: Int): String? {
        val id = interfaceDefinitions.get(interfaceId).stringId
        if (!player.interfaces.contains(id)) {
            logger.info { "Player doesn't have interface open [$player, interface=$id]" }
            return null
        }
        return id
    }

    private fun getComponentDefinition(player: Player, id: Int, componentId: Int): InterfaceComponentDefinition? {
        val interfaceDefinition = interfaceDefinitions.get(id)
        val componentDefinition = interfaceDefinition.components?.get(componentId)
        if (componentDefinition == null) {
            logger.info { "Interface doesn't have component [$player, interface=$id, component=$componentId]" }
            return null
        }
        return componentDefinition
    }

    private fun getInventory(player: Player, id: String, component: String, componentDefinition: InterfaceComponentDefinition): String? {
        if (component.isEmpty()) {
            logger.info { "No inventory component found [$player, interface=$id, inventory=$component]" }
            return null
        }
        if (id == "shop") {
            return player["shop"]
        }
        var inventory = componentDefinition["inventory", ""]
        if (id == "grand_exchange") {
            inventory = "collection_box_${player["grand_exchange_box", -1]}"
        }
        if (!player.inventories.contains(inventory)) {
            logger.info { "Player doesn't have interface inventory [$player, interface=$id, inventory=$inventory]" }
            return null
        }
        return inventory
    }

    data class InterfaceData(
        val id: String,
        val component: String,
        val item: Item,
        val inventory: String,
        val options: Array<String?>?,
    )
}

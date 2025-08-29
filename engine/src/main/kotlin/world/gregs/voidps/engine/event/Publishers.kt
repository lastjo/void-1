package world.gregs.voidps.engine.event

import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.PlayerRights

abstract class Publishers {
    suspend fun option(character: Character, target: Player, option: String, approach: Boolean = false): Boolean {
        return when(character) {
            is Player -> playerPlayerOption(character, target, option, approach) || characterPlayerOption(character, target, option, approach)
            is NPC -> npcPlayerOption(character, target, option, approach) || characterPlayerOption(character, target, option, approach)
            else -> false
        }
    }

    suspend fun option(character: Character, target: NPC, option: String, approach: Boolean = false): Boolean {
        return when(character) {
            is Player -> playerNPCOption(character, target, option, approach) || characterNPCOption(character, target, option, approach)
            is NPC -> npcNPCOption(character, target, option, approach) || characterNPCOption(character, target, option, approach)
            else -> false
        }
    }

    suspend fun option(character: Character, target: GameObject, option: String, approach: Boolean = false): Boolean {
        return when(character) {
            is Player -> playerGameObjectOption(character, target, option, approach) || characterGameObjectOption(character, target, option, approach)
            is NPC -> npcGameObjectOption(character, target, option, approach) || characterGameObjectOption(character, target, option, approach)
            else -> false
        }
    }

    suspend fun option(character: Character, target: FloorItem, option: String, approach: Boolean = false): Boolean {
        return when(character) {
            is Player -> playerFloorItemOption(character, target, option, approach) || characterFloorItemOption(character, target, option, approach)
            is NPC -> npcFloorItemOption(character, target, option, approach) || characterFloorItemOption(character, target, option, approach)
            else -> false
        }
    }

    open suspend fun playerGameObjectOption(player: Player, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open suspend fun playerPlayerOption(player: Player, target: Player, option: String, approach: Boolean = false): Boolean = false
    open suspend fun playerNPCOption(player: Player, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open suspend fun playerFloorItemOption(player: Player, target: FloorItem, option: String, approach: Boolean = false): Boolean = false
    open suspend fun npcGameObjectOption(npc: NPC, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open suspend fun npcPlayerOption(npc: NPC, target: Player, option: String, approach: Boolean = false): Boolean = false
    open suspend fun npcNPCOption(npc: NPC, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open suspend fun npcFloorItemOption(npc: NPC, target: FloorItem, option: String, approach: Boolean = false): Boolean = false
    open suspend fun characterGameObjectOption(character: Character, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open suspend fun characterPlayerOption(character: Character, target: Player, option: String, approach: Boolean = false): Boolean = false
    open suspend fun characterNPCOption(character: Character, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open suspend fun characterFloorItemOption(character: Character, target: FloorItem, option: String, approach: Boolean = false): Boolean = false

    open fun hasPlayerGameObjectOption(player: Player, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open fun hasPlayerPlayerOption(player: Player, target: Player, option: String, approach: Boolean = false): Boolean = false
    open fun hasPlayerNPCOption(player: Player, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open fun hasPlayerFloorItemOption(player: Player, target: FloorItem, option: String, approach: Boolean = false): Boolean = false
    open fun hasNpcGameObjectOption(npc: NPC, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open fun hasNpcPlayerOption(npc: NPC, target: Player, option: String, approach: Boolean = false): Boolean = false
    open fun hasNpcNPCOption(npc: NPC, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open fun hasNpcFloorItemOption(npc: NPC, target: FloorItem, option: String, approach: Boolean = false): Boolean = false
    open fun hasCharacterGameObjectOption(character: Character, target: GameObject, option: String, approach: Boolean = false): Boolean = false
    open fun hasCharacterPlayerOption(character: Character, target: Player, option: String, approach: Boolean = false): Boolean = false
    open fun hasCharacterNPCOption(character: Character, target: NPC, option: String, approach: Boolean = false): Boolean = false
    open fun hasCharacterFloorItemOption(character: Character, target: FloorItem, option: String, approach: Boolean = false): Boolean = false

    open fun interfaceClosed(player: Player, id: String): Boolean = false

    open fun command(player: Player, prefix: String, content: String = "", rights: Int = PlayerRights.NONE): Boolean = false

    suspend fun interfaceOn(player: Player, target: Any, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean {
        return when (target) {
            is Player -> interfaceOnPlayer(player, target, id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
            is NPC -> interfaceOnNPC(player, target, id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
            is Item -> interfaceOnItem(player, target, id, component, item, itemSlot, inventory)
            is FloorItem -> interfaceOnFloorItem(player, target, id, component, item, itemSlot, inventory)
            is GameObject -> interfaceOnGameObject(player, target, id, component, item, itemSlot, inventory)
            else -> false
        }
    }

    open suspend fun interfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open suspend fun interfaceOnNPC(player: Player, target: NPC, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open suspend fun interfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open suspend fun interfaceOnItem(player: Player, target: Item, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open suspend fun interfaceOnGameObject(player: Player, target: GameObject, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open suspend fun interfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun hasInterfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open fun hasInterfaceOnNPC(player: Player, target: NPC, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open fun hasInterfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open fun hasInterfaceOnItem(player: Player, target: Item, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open fun hasInterfaceOnGameObject(player: Player, target: GameObject, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false
    open fun hasInterfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open suspend fun interfaceOption(player: Player, id: String = "", component: String = "", option: String = "", optionIndex: Int = -1, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun itemAdded(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun itemRemoved(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun interfaceOpen(player: Player, id: String = ""): Boolean = false

    open fun spawn(player: Player): Boolean = false
    open fun spawn(npc: NPC): Boolean = false
    open fun spawn(floorItem: FloorItem): Boolean = false
    open fun spawn(obj: GameObject): Boolean = false
    open fun spawn(world: World): Boolean = false

    open fun publish(player: Player, event: String, id: String = ""): Boolean = false

    fun timerStart(source: Entity, timer: String, restart: Boolean = false): Int {
        return when (source) {
            is Player -> {
                var result = timerStartPlayer(source, timer, restart)
                if (result == -1) {
                    result = timerStartCharacter(source, timer, restart)
                }
                result
            }
            is NPC -> {
                var result = timerStartNPC(source, timer, restart)
                if (result == -1) {
                    result = timerStartCharacter(source, timer, restart)
                }
                result
            }
            is Character -> timerStartCharacter(source, timer, restart)
            is World -> timerStartWorld(source, timer, restart)
            else -> -1
        }
    }

    open fun timerStartPlayer(player: Player, timer: String, restart: Boolean = false): Int = -1
    open fun timerStartNPC(npc: NPC, timer: String, restart: Boolean = false): Int = -1
    open fun timerStartCharacter(character: Character, timer: String, restart: Boolean = false): Int = -1
    open fun timerStartWorld(world: World, timer: String, restart: Boolean = false): Int = -1

    fun timerStop(source: Entity, timer: String, logout: Boolean = false): Int {
        return when (source) {
            is Player -> {
                var result = timerStopPlayer(source, timer, logout)
                if (result == -1) {
                    result = timerStopCharacter(source, timer, logout)
                }
                result
            }
            is NPC -> {
                var result = timerStopNPC(source, timer, logout)
                if (result == -1) {
                    result = timerStopCharacter(source, timer, logout)
                }
                result
            }
            is World -> timerStopWorld(source, timer, logout)
            else -> -1
        }
    }

    open fun timerStopPlayer(player: Player, timer: String, logout: Boolean = false): Int = -1
    open fun timerStopNPC(npc: NPC, timer: String, logout: Boolean = false): Int = -1
    open fun timerStopCharacter(character: Character, timer: String, logout: Boolean = false): Int = -1
    open fun timerStopWorld(world: World, timer: String, logout: Boolean = false): Int = -1

    fun timerTick(source: Entity, timer: String): Int {
        return when (source) {
            is Player -> {
                var result = timerTickPlayer(source, timer)
                if (result == -1) {
                    result = timerTickCharacter(source, timer)
                }
                result
            }
            is NPC -> {
                var result = timerTickNPC(source, timer)
                if (result == -1) {
                    result = timerTickCharacter(source, timer)
                }
                result
            }
            is World -> timerTickWorld(source, timer)
            else -> -1
        }
    }

    open fun timerTickPlayer(player: Player, timer: String): Int = -1
    open fun timerTickNPC(npc: NPC, timer: String): Int = -1
    open fun timerTickCharacter(character: Character, timer: String): Int = -1
    open fun timerTickWorld(world: World, timer: String): Int = -1
}
package world.gregs.voidps.engine.event

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import world.gregs.voidps.cache.definition.data.NPCDefinition
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.ui.event.CloseInterface
import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.network.client.Instruction
import world.gregs.voidps.type.Area
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.area.Rectangle
import kotlin.coroutines.cancellation.CancellationException

interface Publishers {
    suspend fun option(character: Character, target: Player, option: String, approach: Boolean = false): Boolean = when (character) {
        is Player -> playerPlayerOption(character, target, option, approach) || characterPlayerOption(character, target, option, approach)
        is NPC -> npcPlayerOption(character, target, option, approach) || characterPlayerOption(character, target, option, approach)
        else -> false
    }

    suspend fun option(character: Character, target: NPC, option: String, approach: Boolean = false): Boolean = when (character) {
        is Player -> {
            val def = target.def(character)
            playerNPCOption(character, target, def, option, approach) || characterNPCOption(character, target, def, option, approach)
        }
        is NPC -> npcNPCOption(character, target, target.def, option, approach) || characterNPCOption(character, target, target.def, option, approach)
        else -> false
    }

    suspend fun option(character: Character, target: GameObject, option: String, approach: Boolean = false): Boolean = when (character) {
        is Player -> {
            val def = target.def(character)
            playerGameObjectOption(character, target, def, option, approach) || characterGameObjectOption(character, target, def, option, approach)
        }
        is NPC -> npcGameObjectOption(character, target, target.def, option, approach) || characterGameObjectOption(character, target, target.def, option, approach)
        else -> false
    }

    suspend fun option(character: Character, target: FloorItem, option: String, approach: Boolean = false): Boolean = when (character) {
        is Player -> playerFloorItemOption(character, target, option, approach) || characterFloorItemOption(character, target, option, approach)
        is NPC -> npcFloorItemOption(character, target, option, approach) || characterFloorItemOption(character, target, option, approach)
        else -> false
    }

    suspend fun playerPlayerOption(player: Player, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun npcPlayerOption(npc: NPC, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun characterPlayerOption(character: Character, target: Player, option: String = "", approach: Boolean = false): Boolean = false

    fun hasPlayerPlayerOption(player: Player, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    fun hasNPCPlayerOption(npc: NPC, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    fun hasCharacterPlayerOption(character: Character, target: Player, option: String = "", approach: Boolean = false): Boolean = false

    suspend fun playerGameObjectOption(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun npcGameObjectOption(npc: NPC, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun characterGameObjectOption(character: Character, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    fun hasPlayerGameObjectOption(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    fun hasNPCGameObjectOption(npc: NPC, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    fun hasCharacterGameObjectOption(character: Character, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    suspend fun playerNPCOption(player: Player, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun npcNPCOption(npc: NPC, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun characterNPCOption(character: Character, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    fun hasPlayerNPCOption(player: Player, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    fun hasNPCNPCOption(npc: NPC, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    fun hasCharacterNPCOption(character: Character, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    suspend fun playerFloorItemOption(player: Player, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun npcFloorItemOption(npc: NPC, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    suspend fun characterFloorItemOption(character: Character, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false

    fun hasPlayerFloorItemOption(player: Player, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    fun hasNPCFloorItemOption(npc: NPC, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    fun hasCharacterFloorItemOption(character: Character, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false

    /**
     * Notification that an interface was opened.
     * @see [interfaceRefreshed] for re-opened interfaces
     */
    fun interfaceOpened(player: Player, id: String = ""): Boolean = false

    /**
     * An interface was open and has now been closed
     */
    fun interfaceClosed(player: Player, id: String = ""): Boolean = false

    /**
     * When an interface is initially opened or opened again
     * Primarily for interface changes like unlocking.
     */
    fun interfaceRefreshed(player: Player, id: String = ""): Boolean = false

    suspend fun command(player: Player, content: String = "", prefix: String = "", rights: Int = PlayerRights.NONE): Boolean = false

    suspend fun interfaceOn(player: Player, target: Any, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = when (target) {
        is Player -> interfaceOnPlayer(player, target, id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
        is NPC -> interfaceOnNPC(player, target, target.def(player), id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
        is FloorItem -> interfaceOnFloorItem(player, target, id, component, item, itemSlot, inventory)
        is GameObject -> interfaceOnGameObject(player, target, target.def(player), id, component, item, itemSlot, inventory)
        else -> false
    }

    suspend fun interfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    suspend fun interfaceOnNPC(player: Player, target: NPC, def: NPCDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    suspend fun interfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    suspend fun interfaceOnGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    suspend fun interfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun interfaceOnItem(player: Player, toItem: Item, id: String = "", component: String = "", toSlot: Int = -1, fromItem: Item = Item.EMPTY, fromSlot: Int = -1, fromInventory: String = "", toInventory: String = "", approach: Boolean = false): Boolean = false

    fun hasInterfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun hasInterfaceOnNPC(player: Player, target: NPC, def: NPCDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun hasInterfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun hasInterfaceOnGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun hasInterfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    fun hasInterfaceOnItem(player: Player, toItem: Item, id: String = "", component: String = "", toSlot: Int = -1, fromItem: Item = Item.EMPTY, fromSlot: Int = -1, fromInventory: String = "", toInventory: String = "", approach: Boolean = false): Boolean = false

    suspend fun interfaceOption(player: Player, id: String = "", component: String = "", option: String = "", optionIndex: Int = -1, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    fun continueDialogue(player: Player, id: String = "", component: String = ""): Boolean = false
    fun continueDialogueItem(player: Player, item: Item): Boolean = false

    suspend fun inventoryOption(player: Player, item: Item = Item.EMPTY, inventory: String = "", option: String = "", itemSlot: Int = -1): Boolean = false

    fun inventoryChanged(player: Player, inventory: String = "", itemSlot: Int = -1, item: Item = Item.EMPTY, from: String = "", fromSlot: Int = -1, fromItem: Item = Item.EMPTY): Boolean = false
    fun inventoryUpdated(player: Player, inventory: String = ""): Boolean = false

    fun inventorySwap(player: Player, id: String = "", component: String = "", fromItem: Item = Item.EMPTY, fromSlot: Int = -1, fromInventory: String = "", toId: String = "", toComponent: String = "", toItem: Item = Item.EMPTY, toSlot: Int = -1, toInventory: String = ""): Boolean = false

    fun itemAdded(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    fun itemRemoved(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    fun spawnPlayer(player: Player): Boolean = false
    fun spawnNPC(npc: NPC): Boolean = false
    fun spawnCharacter(character: Character): Boolean = false
    fun spawnFloorItem(floorItem: FloorItem): Boolean = false
    fun spawnGameObject(obj: GameObject): Boolean = false
    fun spawnWorld(world: World): Boolean = false

    fun despawnPlayer(player: Player): Boolean = false
    fun despawnNPC(npc: NPC): Boolean = false
    fun despawnCharacter(character: Character): Boolean = false
    fun despawnFloorItem(floorItem: FloorItem): Boolean = false
    fun despawnGameObject(obj: GameObject): Boolean = false
    fun despawnWorld(world: World): Boolean = false

    fun playerDeath(player: Player): Boolean = false
    fun npcDeath(npc: NPC): Boolean = false
    fun characterDeath(character: Character): Boolean = false

    fun publish(event: String = "", id: Any = ""): Boolean = false
    fun publishPlayer(player: Player, event: String = "", id: Any = ""): Boolean = false
    fun publishNPC(npc: NPC, event: String = "", id: Any = ""): Boolean = false
    fun publishFloorItem(floorItem: FloorItem, event: String = "", id: Any = ""): Boolean = false
    fun publishGameObject(obj: GameObject, event: String = "", id: Any = ""): Boolean = false
    fun publishWorld(world: World, event: String = "", id: Any = ""): Boolean = false

    fun timerStart(source: Entity, timer: String, restart: Boolean = false): Int = when (source) {
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

    fun timerStartPlayer(player: Player, timer: String = "", restart: Boolean = false): Int = -1
    fun timerStartNPC(npc: NPC, timer: String = "", restart: Boolean = false): Int = -1
    fun timerStartCharacter(character: Character, timer: String = "", restart: Boolean = false): Int = -1
    fun timerStartWorld(world: World, timer: String = "", restart: Boolean = false): Int = -1

    fun timerStop(source: Entity, timer: String, logout: Boolean = false): Boolean = when (source) {
        is Player -> timerStopPlayer(source, timer, logout) || timerStopCharacter(source, timer, logout)
        is NPC -> timerStopNPC(source, timer, logout) || timerStopCharacter(source, timer, logout)
        is World -> timerStopWorld(source, timer, logout)
        else -> false
    }

    fun timerStopPlayer(player: Player, timer: String = "", logout: Boolean = false): Boolean = false
    fun timerStopNPC(npc: NPC, timer: String = "", logout: Boolean = false): Boolean = false
    fun timerStopCharacter(character: Character, timer: String = "", logout: Boolean = false): Boolean = false
    fun timerStopWorld(world: World, timer: String = "", logout: Boolean = false): Boolean = false

    fun timerTick(source: Entity, timer: String): Int = when (source) {
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

    fun timerTickPlayer(player: Player, timer: String = ""): Int = -1
    fun timerTickNPC(npc: NPC, timer: String = ""): Int = -1
    fun timerTickCharacter(character: Character, timer: String = ""): Int = -1
    fun timerTickWorld(world: World, timer: String = ""): Int = -1

    fun teleport(player: Player, type: String = ""): Int = 0
    fun teleportLand(player: Player, type: String = ""): Boolean = false

    fun teleportGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = ""): Int = 0
    fun teleportLandGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = ""): Boolean = false

    suspend fun enterArea(player: Player, name: String = "", tags: Set<String> = emptySet(), area: Area = Rectangle(0, 0, 0, 0)): Boolean = false
    suspend fun exitArea(player: Player, name: String = "", tags: Set<String> = emptySet(), area: Area = Rectangle(0, 0, 0, 0), logout: Boolean = false): Boolean = false

    suspend fun movePlayer(player: Player, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false
    suspend fun moveNPC(npc: NPC, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false
    suspend fun moveCharacter(character: Character, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false

    fun experience(player: Player, skill: Skill = Skill.Attack, from: Double = 0.0, to: Double = 0.0, blocked: Boolean = false): Boolean = false

    /**
     * Notification when current skill level has changed.
     */
    fun levelChangePlayer(player: Player, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false
    fun levelChangeNPC(npc: NPC, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false
    fun levelChangeCharacter(character: Character, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false

    /**
     * Variable with name [key] was set to [to]
     * @param from previous value
     */
    fun variableSet(entity: Entity, id: String = "", from: Any? = null, to: Any? = null): Boolean {
        when (entity) {
            is Player -> {
                variableSetPlayer(entity, id, from, to)
                variableSetCharacter(entity, id, from, to)
            }
            is NPC -> {
                variableSetNPC(entity, id, from, to)
                variableSetCharacter(entity, id, from, to)
            }
        }
        return false
    }

    fun variableSetPlayer(player: Player, id: String = "", from: Any? = null, to: Any? = null): Boolean = false
    fun variableSetNPC(npc: NPC, id: String = "", from: Any? = null, to: Any? = null): Boolean = false
    fun variableSetCharacter(character: Character, id: String = "", from: Any? = null, to: Any? = null): Boolean = false

    fun variableBits(entity: Entity, id: String = "", value: Any? = null, added: Boolean = true): Boolean {
        when (entity) {
            is Player -> {
                variableBitsPlayer(entity, id, value, added)
                variableBitsCharacter(entity, id, value, added)
            }
            is NPC -> {
                variableBitsNPC(entity, id, value, added)
                variableBitsCharacter(entity, id, value, added)
            }
        }
        return false
    }

    fun variableBitsPlayer(player: Player, id: String = "", value: Any? = null, added: Boolean = true): Boolean = false
    fun variableBitsNPC(npc: NPC, id: String = "", value: Any? = null, added: Boolean = true): Boolean = false
    fun variableBitsCharacter(character: Character, id: String = "", value: Any? = null, added: Boolean = true): Boolean = false

    fun playerCombatAttackPlayer(player: Player, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun playerCombatAttackNPC(player: Player, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun playerCombatAttackCharacter(player: Player, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false

    fun combatAttack(source: Character, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean {
        when (source) {
            is Player -> {
                when (target) {
                    is Player -> playerCombatAttackPlayer(source, target, type, damage, weapon, spell, special, delay, stage)
                    is NPC -> playerCombatAttackNPC(source, target, type, damage, weapon, spell, special, delay, stage)
                }
                playerCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay, stage)
            }
            is NPC -> {
                when (target) {
                    is Player -> npcCombatAttackPlayer(source, target, type, damage, weapon, spell, special, delay, stage)
                    is NPC -> npcCombatAttackNPC(source, target, type, damage, weapon, spell, special, delay, stage)
                }
                npcCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay, stage)
            }
        }
        characterCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay, stage)
        return true
    }

    fun npcCombatAttackPlayer(source: NPC, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun npcCombatAttackNPC(source: NPC, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun npcCombatAttackCharacter(source: NPC, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false

    fun characterCombatAttackPlayer(source: Character, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun characterCombatAttackNPC(source: Character, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false
    fun characterCombatAttackCharacter(source: Character, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1, stage: Int = -1): Boolean = false

    fun prayerStartPlayer(player: Player, id: String = "", restart: Boolean = false): Boolean = false
    fun prayerStartNPC(npc: NPC, id: String = "", restart: Boolean = false): Boolean = false
    fun prayerStartCharacter(character: Character, id: String = "", restart: Boolean = false): Boolean = false

    fun prayerStopPlayer(player: Player, id: String = "", restart: Boolean = false): Boolean = false
    fun prayerStopNPC(npc: NPC, id: String = "", restart: Boolean = false): Boolean = false
    fun prayerStopCharacter(character: Character, id: String = "", restart: Boolean = false): Boolean = false

    fun consume(player: Player, item: Item = Item.EMPTY, slot: Int = -1): Boolean = true

    fun playerTakeItem(player: Player, id: String = ""): String = id
    fun npcTakeItem(npc: NPC, id: String = ""): String = id

    fun huntPlayer(npc: NPC, target: Player, mode: String = ""): Boolean = false
    fun huntNpc(npc: NPC, target: NPC, mode: String = ""): Boolean = false
    fun huntFloorItem(npc: NPC, target: FloorItem, mode: String = ""): Boolean = false
    fun huntGameObject(npc: NPC, target: GameObject, mode: String = ""): Boolean = false

    fun specialAttack(player: Player, target: Character, id: String = "", damage: Int = -2): Boolean = false
    fun specialAttackPrepare(player: Player, id: String = ""): Boolean = false

    fun message(player: Player, type: String, source: String, rights: Int, effects: Int, message: String, compressed: ByteArray): Boolean = false

    fun instruction(player: Player, instruction: Instruction = object : Instruction {}): Boolean = false

    companion object {

        private val logger = InlineLogger()
        private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        private val errorHandler = CoroutineExceptionHandler { _, throwable ->
            if (throwable !is CancellationException) {
                logger.warn(throwable) { "Error in publisher." }
            }
        }

        fun launch(block: suspend CoroutineScope.() -> Unit) {
            scope.launch(errorHandler, block = block)
        }

        var all: Publishers = object : Publishers {}
            private set

        fun set(publishers: Publishers) {
            this.all = publishers
        }

        fun clear() {
            set(object : Publishers {})
        }
    }
}

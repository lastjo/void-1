package world.gregs.voidps.engine.event

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import world.gregs.voidps.cache.definition.data.ItemDefinition
import world.gregs.voidps.cache.definition.data.NPCDefinition
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.entity.World
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.type.Area
import world.gregs.voidps.type.PlayerRights
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.area.Rectangle
import kotlin.coroutines.cancellation.CancellationException

abstract class Publishers {
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

    open suspend fun playerPlayerOption(player: Player, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun npcPlayerOption(npc: NPC, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun characterPlayerOption(character: Character, target: Player, option: String = "", approach: Boolean = false): Boolean = false

    open fun hasPlayerPlayerOption(player: Player, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasNPCPlayerOption(npc: NPC, target: Player, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasCharacterPlayerOption(character: Character, target: Player, option: String = "", approach: Boolean = false): Boolean = false

    open suspend fun playerGameObjectOption(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun npcGameObjectOption(npc: NPC, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun characterGameObjectOption(character: Character, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    open fun hasPlayerGameObjectOption(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasNPCGameObjectOption(npc: NPC, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasCharacterGameObjectOption(character: Character, target: GameObject, def: ObjectDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    open suspend fun playerNPCOption(player: Player, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun npcNPCOption(npc: NPC, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun characterNPCOption(character: Character, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    open fun hasPlayerNPCOption(player: Player, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasNPCNPCOption(npc: NPC, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasCharacterNPCOption(character: Character, target: NPC, def: NPCDefinition = target.def, option: String = "", approach: Boolean = false): Boolean = false

    open suspend fun playerFloorItemOption(player: Player, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun npcFloorItemOption(npc: NPC, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    open suspend fun characterFloorItemOption(character: Character, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false

    open fun hasPlayerFloorItemOption(player: Player, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasNPCFloorItemOption(npc: NPC, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false
    open fun hasCharacterFloorItemOption(character: Character, target: FloorItem, option: String = "", approach: Boolean = false): Boolean = false

    open fun interfaceOpened(player: Player, id: String = ""): Boolean = false
    open fun interfaceClosed(player: Player, id: String = ""): Boolean = false
    open fun interfaceRefreshed(player: Player, id: String): Boolean = false

    open fun command(player: Player, content: String = "", prefix: String = "", rights: Int = PlayerRights.NONE): Boolean = false

    suspend fun interfaceOn(player: Player, target: Any, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = when (target) {
        is Player -> interfaceOnPlayer(player, target, id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
        is NPC -> interfaceOnNPC(player, target, target.def(player), id, component, item, itemSlot, inventory) || interfaceOnCharacter(player, target, id, component, item, itemSlot, inventory)
        is FloorItem -> interfaceOnFloorItem(player, target, id, component, item, itemSlot, inventory)
        is GameObject -> interfaceOnGameObject(player, target, target.def(player), id, component, item, itemSlot, inventory)
        else -> false
    }

    open suspend fun interfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open suspend fun interfaceOnNPC(player: Player, target: NPC, def: NPCDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open suspend fun interfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open suspend fun interfaceOnGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open suspend fun interfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun interfaceOnItem(player: Player, toItem: Item, toSlot: Int = -1, fromItem: Item = Item.EMPTY, fromSlot: Int = -1, fromInventory: String = "", toInventory: String = "", approach: Boolean = false): Boolean = false

    open fun hasInterfaceOnPlayer(player: Player, target: Player, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun hasInterfaceOnNPC(player: Player, target: NPC, def: NPCDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun hasInterfaceOnCharacter(player: Player, target: Character, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun hasInterfaceOnGameObject(player: Player, target: GameObject, def: ObjectDefinition = target.def, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun hasInterfaceOnFloorItem(player: Player, target: FloorItem, id: String = "", component: String = "", item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = "", approach: Boolean = false): Boolean = false
    open fun hasInterfaceOnItem(player: Player, toItem: Item, toSlot: Int = -1, fromItem: Item = Item.EMPTY, fromSlot: Int = -1, fromInventory: String = "", toInventory: String = "", approach: Boolean = false): Boolean = false

    open suspend fun interfaceOption(player: Player, id: String = "", component: String = "", option: String = "", optionIndex: Int = -1, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open suspend fun inventoryOption(player: Player, item: Item = Item.EMPTY, inventory: String = "", option: String = "", itemSlot: Int = -1): Boolean = false

    open fun inventoryChanged(player: Player, inventory: String = "", index: Int = -1, item: Item = Item.EMPTY, from: String = "", fromIndex: Int = -1, fromItem: Item = Item.EMPTY): Boolean = false

    open fun itemAdded(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun itemRemoved(player: Player, item: Item = Item.EMPTY, itemSlot: Int = -1, inventory: String = ""): Boolean = false

    open fun spawnPlayer(player: Player): Boolean = false
    open fun spawnNPC(npc: NPC): Boolean = false
    open fun spawnCharacter(character: Character): Boolean = false
    open fun spawnFloorItem(floorItem: FloorItem): Boolean = false
    open fun spawnGameObject(obj: GameObject): Boolean = false
    open fun spawnWorld(world: World): Boolean = false

    open fun despawnPlayer(player: Player): Boolean = false
    open fun despawnNPC(npc: NPC): Boolean = false
    open fun despawnCharacter(character: Character): Boolean = false
    open fun despawnFloorItem(floorItem: FloorItem): Boolean = false
    open fun despawnGameObject(obj: GameObject): Boolean = false
    open fun despawnWorld(world: World): Boolean = false

    open fun playerDeath(player: Player): Boolean = false
    open fun npcDeath(npc: NPC): Boolean = false
    open fun characterDeath(character: Character): Boolean = false

    open fun publish(event: String = "", id: String = ""): Boolean = false
    open fun publishPlayer(player: Player, event: String = "", id: String = ""): Boolean = false
    open fun publishNPC(npc: NPC, def: NPCDefinition = npc.def, event: String = "", id: String = ""): Boolean = false
    open fun publishFloorItem(floorItem: FloorItem, event: String = "", id: String = ""): Boolean = false
    open fun publishGameObject(obj: GameObject, def: ObjectDefinition = obj.def, event: String = "", id: String = ""): Boolean = false
    open fun publishWorld(world: World, event: String = "", id: String = ""): Boolean = false

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

    open fun timerStartPlayer(player: Player, timer: String = "", restart: Boolean = false): Int = -1
    open fun timerStartNPC(npc: NPC, timer: String = "", restart: Boolean = false): Int = -1
    open fun timerStartCharacter(character: Character, timer: String = "", restart: Boolean = false): Int = -1
    open fun timerStartWorld(world: World, timer: String = "", restart: Boolean = false): Int = -1

    fun timerStop(source: Entity, timer: String, logout: Boolean = false): Boolean = when (source) {
        is Player -> timerStopPlayer(source, timer, logout) || timerStopCharacter(source, timer, logout)
        is NPC -> timerStopNPC(source, timer, logout) || timerStopCharacter(source, timer, logout)
        is World -> timerStopWorld(source, timer, logout)
        else -> false
    }

    open fun timerStopPlayer(player: Player, timer: String = "", logout: Boolean = false): Boolean = false
    open fun timerStopNPC(npc: NPC, timer: String = "", logout: Boolean = false): Boolean = false
    open fun timerStopCharacter(character: Character, timer: String = "", logout: Boolean = false): Boolean = false
    open fun timerStopWorld(world: World, timer: String = "", logout: Boolean = false): Boolean = false

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

    open fun timerTickPlayer(player: Player, timer: String = ""): Int = -1
    open fun timerTickNPC(npc: NPC, timer: String = ""): Int = -1
    open fun timerTickCharacter(character: Character, timer: String = ""): Int = -1
    open fun timerTickWorld(world: World, timer: String = ""): Int = -1

    open fun teleport(player: Player, target: GameObject, def: ObjectDefinition = target.def, option: String = "", land: Boolean = false): Boolean = false

    open fun enterArea(player: Player, name: String = "", tags: Set<String> = emptySet(), area: Area = Rectangle(0, 0, 0, 0)): Boolean = false
    open fun exitArea(player: Player, name: String = "", tags: Set<String> = emptySet(), area: Area = Rectangle(0, 0, 0, 0)): Boolean = false

    open fun movePlayer(player: Player, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false
    open fun moveNPC(npc: NPC, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false
    open fun moveCharacter(character: Character, from: Tile = Tile.EMPTY, to: Tile = Tile.EMPTY): Boolean = false

    open fun levelChangePlayer(player: Player, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false
    open fun levelChangeNPC(npc: NPC, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false
    open fun levelChangeCharacter(character: Character, skill: Skill = Skill.Attack, from: Int = -1, to: Int = -1, max: Boolean = false): Boolean = false

    open fun variableSet(entity: Entity, id: String = "", from: Any? = null, to: Any? = null): Boolean {
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

    open fun variableSetPlayer(player: Player, id: String = "", from: Any? = null, to: Any? = null): Boolean = false
    open fun variableSetNPC(npc: NPC, id: String = "", from: Any? = null, to: Any? = null): Boolean = false
    open fun variableSetCharacter(character: Character, id: String = "", from: Any? = null, to: Any? = null): Boolean = false

    open fun playerCombatAttackPlayer(player: Player, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun playerCombatAttackNPC(player: Player, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun playerCombatAttackCharacter(player: Player, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false

    open fun combatAttack(source: Character, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean {
        when (source) {
            is Player -> {
                when (target) {
                    is Player -> playerCombatAttackPlayer(source, target, type, damage, weapon, spell, special, delay)
                    is NPC -> playerCombatAttackNPC(source, target, type, damage, weapon, spell, special, delay)
                }
                playerCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay)
            }
            is NPC -> {
                when (target) {
                    is Player -> npcCombatAttackPlayer(source, target, type, damage, weapon, spell, special, delay)
                    is NPC -> npcCombatAttackNPC(source, target, type, damage, weapon, spell, special, delay)
                }
                npcCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay)
            }
        }
        characterCombatAttackCharacter(source, target, type, damage, weapon, spell, special, delay)
        return true
    }

    open fun npcCombatAttackPlayer(source: NPC, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun npcCombatAttackNPC(source: NPC, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun npcCombatAttackCharacter(source: NPC, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false

    open fun characterCombatAttackPlayer(source: Character, target: Player, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun characterCombatAttackNPC(source: Character, target: NPC, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false
    open fun characterCombatAttackCharacter(source: Character, target: Character, type: String = "", damage: Int = -1, weapon: Item = Item.EMPTY, spell: String = "", special: Boolean = false, delay: Int = -1): Boolean = false

    open fun prayerStartPlayer(player: Player, id: String = "", restart: Boolean = false): Boolean = false
    open fun prayerStartNPC(npc: NPC, id: String = "", restart: Boolean = false): Boolean = false
    open fun prayerStartCharacter(character: Character, id: String = "", restart: Boolean = false): Boolean = false

    open fun prayerStopPlayer(player: Player, id: String = "", restart: Boolean = false): Boolean = false
    open fun prayerStopNPC(npc: NPC, id: String = "", restart: Boolean = false): Boolean = false
    open fun prayerStopCharacter(character: Character, id: String = "", restart: Boolean = false): Boolean = false

    open fun consume(player: Player, item: Item = Item.EMPTY, slot: Int = -1): Boolean = true

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

        var all: Publishers = object : Publishers() {}
            private set

        fun set(publishers: Publishers) {
            this.all = publishers
        }

        fun clear() {
            set(object : Publishers() {})
        }
    }

    open fun playerTakeItem(player: Player, id: String = ""): String = id
    open fun npcTakeItem(npc: NPC, id: String = ""): String = id
}

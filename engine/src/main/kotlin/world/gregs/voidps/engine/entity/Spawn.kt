package world.gregs.voidps.engine.entity

import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.floor.FloorItem
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.event.Event
import world.gregs.voidps.engine.event.EventDispatcher
import world.gregs.voidps.engine.event.Events

object Spawn : Event {

    override val notification: Boolean = true

    override val size = 2

    override fun parameter(dispatcher: EventDispatcher, index: Int) = when (index) {
        0 -> "${dispatcher.key}_spawn"
        1 -> dispatcher.identifier
        else -> null
    }
}

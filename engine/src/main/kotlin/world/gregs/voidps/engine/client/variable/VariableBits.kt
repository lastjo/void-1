package world.gregs.voidps.engine.client.variable

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import world.gregs.voidps.engine.entity.Entity
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.event.Publishers

class VariableBits(
    private val variables: Variables,
    private val entity: Entity,
) {

    fun contains(key: String, id: Any): Boolean {
        val value: List<Any> = variables.get(key) ?: return false
        return value.contains(id)
    }

    fun set(key: String, value: Any, refresh: Boolean): Boolean {
        val values: MutableList<Any> = variables.getOrPut(key) { ObjectArrayList() }
        if (!values.contains(value) && values.add(value)) {
            if (refresh) {
                variables.send(key)
            }
            Publishers.all.variableBits(entity, key, value, added = true)
            return true
        }
        return false
    }

    fun remove(key: String, value: Any, refresh: Boolean): Boolean {
        val values: MutableList<Any> = variables.get(key) ?: return false
        if (values.remove(value)) {
            if (refresh) {
                variables.send(key)
            }
            Publishers.all.variableBits(entity, key, value, added = false)
            return true
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun clear(key: String, refresh: Boolean) {
        val values = variables.clear(key, refresh) as? List<Any> ?: return
        if (refresh) {
            variables.send(key)
        }
        for (value in values) {
            Publishers.all.variableBits(entity, key, value, added = false)
        }
    }
}

package content.skill.magic.shield

import content.entity.player.dialogue.type.choice
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.chat.plural
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.chat.inventoryFull
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.*
import world.gregs.voidps.engine.inv.transact.TransactionError
import world.gregs.voidps.engine.inv.transact.charge
import world.gregs.voidps.engine.inv.transact.discharge
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.inv.transact.operation.RemoveItem.remove
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.*
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import kotlin.math.min

class CelestialSurgeBox {

    @Inventory("Check*", "celestial_surgebox*")
    suspend fun check(player: Player, item: Item, itemSlot: Int) = player.dialogue {
        val charges = player.inventory.charges(player, itemSlot)
        val dungeoneering = if (item.id == "celestial_surgebox") "" else "_dungeoneering"
        val surge = player["celestial_surgebox_mode$dungeoneering", false]
        choice("The box is currently charged with $charges ${if (surge) "Surge" else "Wave"} ${"spell".plural(charges)}.") {
            option("I want to empty the ${if (surge) "Surge" else "Wave"} spells.", filter = { charges > 0 }) {
                // TODO proper message
                if (emptyRunes(player, surge, dungeoneering, itemSlot, charges)) {
                    player.message("You empty the box of ${if (surge) "Surge" else "Wave"} spells.") // TODO proper message
                } else {
                    player.inventoryFull()
                }
            }
            option("I do not wish to change the box settings.", filter = { charges == 0 })
            option("Switch to ${if (surge) "Wave" else "Surge"}.") {
                if (charges == 0 || emptyRunes(player, surge, dungeoneering, itemSlot, charges)) {
                    val surgeMode = player.toggle("celestial_surgebox_mode$dungeoneering")
                    player.message("This box is set to be charged with ${if (surgeMode) "Surge" else "Wave"} spells.")
                } else {
                    player.inventoryFull()
                }
            }
        }
    }

    @Inventory("Check-charges", "celestial_surgebox*", "worn_equipment")
    fun check(player: Player, item: Item) {
        val surge = player["celestial_surgebox_mode", false]
        val charges = player.equipment.charges(player, EquipSlot.Shield.index)
        player.message("The box is currently charged with $charges ${if (surge) "Surge" else "Wave"} ${"spell".plural(charges)}.") // TODO proper message
    }

    @Spawn
    fun spawn(player: Player) {
        val box = player.equipped(EquipSlot.Shield).id
        if (box.startsWith("celestial_surgebox")) {
            updateCharges(player, EquipSlot.Shield.index, box != "celestial_surgebox")
        } else {
            setCharges(player, 0, box != "celestial_surgebox")
        }
    }

    @ItemAdded("celestial_surgebox*", slots = [EquipSlot.SHIELD], inventory = "worn_equipment")
    fun added(player: Player, item: Item, itemSlot: Int) {
        updateCharges(player, itemSlot, item.id != "celestial_surgebox")
    }

    @ItemRemoved("celestial_surgebox*", slots = [EquipSlot.SHIELD], inventory = "worn_equipment")
    fun removed(player: Player, item: Item) {
        setCharges(player, 0, item.id != "celestial_surgebox")
    }

    @Combat(spell = "*_wave")
    @Combat(spell = "*_surge")
    fun cast(player: Player, target: Character) {
        val box = player.equipped(EquipSlot.Shield).id
        if (box.startsWith("celestial_surgebox")) {
            updateCharges(player, EquipSlot.Shield.index, box != "celestial_surgebox")
        }
    }

    fun emptyRunes(player: Player, surge: Boolean, dungeoneering: String, slot: Int, charges: Int): Boolean = player.inventory.transaction {
        add("air_rune$dungeoneering", charges * if (surge) 7 else 5)
        add("blood_rune$dungeoneering", charges)
        if (surge) {
            add("death_rune$dungeoneering", charges)
        }
        discharge(player, slot, amount = charges)
    }

    fun updateCharges(player: Player, index: Int, dungeoneering: Boolean) {
        val charges = player.equipment.charges(player, index)
        setCharges(player, charges, dungeoneering)
    }

    fun setCharges(player: Player, charges: Int, dungeoneering: Boolean) {
        val type = if (player["celestial_surgebox_mode${if (dungeoneering) "_dungeoneering" else ""}", false]) "surge" else "wave"
        player["celestial_surgebox_$type"] = charges
    }

    @UseOn("air_rune", "celestial_surgebox*")
    @UseOn("blood_rune", "celestial_surgebox*")
    @UseOn("death_rune", "celestial_surgebox*")
    fun use(player: Player, fromItem: Item, toItem: Item, toSlot: Int) {
        charge(player, toItem, toSlot)
    }

    @Inventory("Charge", "celestial_surgebox*")
    fun charge(player: Player, item: Item, itemSlot: Int) {
        val dungeoneering = if (item.id == "celestial_surgebox") "" else "_dungeoneering"
        val surge = player["celestial_surgebox_mode$dungeoneering", false]
        val maximum: Int = item.def.getOrNull("charges_max") ?: item.def.getOrNull("charges") ?: return
        val charges = player.inventory.charges(player, itemSlot)
        player.inventory.transaction {
            val actual = (
                if (surge) {
                    minOf(inventory.count("air_rune$dungeoneering") / 7, inventory.count("blood_rune$dungeoneering"), inventory.count("death_rune$dungeoneering"))
                } else {
                    min(inventory.count("air_rune$dungeoneering") / 5, inventory.count("blood_rune$dungeoneering"))
                }
                ).coerceAtMost(maximum - charges)

            remove("air_rune$dungeoneering", actual * if (surge) 7 else 5)
            remove("blood_rune$dungeoneering", actual)
            if (surge) {
                remove("death_rune$dungeoneering", actual)
            }
            charge(player, itemSlot, actual)
        }
        if (player.inventory.transaction.error != TransactionError.None) {
            player.message("You don't have enough runes to charge the box.") // TODO proper message
        }
    }
}

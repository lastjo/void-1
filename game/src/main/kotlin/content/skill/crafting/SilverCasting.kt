package content.skill.crafting

import content.entity.player.dialogue.type.intEntry
import content.quest.quest
import net.pearx.kasechange.toTitleCase
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.sendScript
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.data.definition.ItemDefinitions
import world.gregs.voidps.engine.data.definition.data.Silver
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.character.player.skill.level.Level.has
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.inv.*
import world.gregs.voidps.engine.queue.weakQueue
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.UseOn

class SilverCasting(private val itemDefinitions: ItemDefinitions) {

    val moulds = listOf(
        Item("holy_mould"),
        Item("sickle_mould"),
        Item("tiara_mould"),
        Item("demonic_sigil_mould"),
        Item("chain_link_mould"),
        Item("unholy_mould"),
        Item("conductor_mould"),
        Item("rod_clay_mould"),
        Item("bolt_mould"),
        Item("key_mould"),
    )

    val Item.silver: Silver?
        get() = def.getOrNull("silver_jewellery")

    @Open("silver_mould")
    fun open(player: Player, id: String) {
        for (mould in moulds) {
            val silver = mould.silver ?: continue
            val item = silver.item
            val quest = silver.quest
            player.interfaces.sendVisibility(id, mould.id, quest == null || player.quest(quest) != "unstarted")
            val has = player.holdsItem(mould.id)
            player.interfaces.sendText(
                id,
                "${mould.id}_text",
                if (has) {
                    val colour = if (has && player.holdsItem("silver_bar")) "green" else "orange"
                    "<$colour>Make ${itemDefinitions.get(item).name.toTitleCase()}"
                } else {
                    "<orange>You need a ${silver.name ?: mould.def.name.lowercase()} to make this item."
                },
            )
            player.interfaces.sendItem(id, "${mould.id}_model", if (has) itemDefinitions.get(item).id else mould.def.id)
        }
    }

    @UseOn("silver_bar", "furnace*", arrive = false)
    fun silver(player: Player, target: GameObject) {
        player.open("silver_mould")
    }

    @UseOn(on = "furnace*", arrive = false)
    fun use(player: Player, target: GameObject, item: Item) {
        if (!item.def.contains("silver_jewellery")) {
            return
        }
        player.make(item, 1)
    }

    @Interface(component = "*_button", id = "silver_mould")
    suspend fun click(player: Player, component: String, option: String) {
        val amount = when (option) {
            "Make 1" -> 1
            "Make 5" -> 5
            "Make All" -> 28
            "Make X" -> player.intEntry("Enter amount:")
            else -> return
        }
        player.make(Item(component.removeSuffix("_button")), amount)
    }

    @Close("silver_mould")
    fun close(player: Player) {
        player.sendScript("clear_dialogues")
    }

    fun Player.make(item: Item, amount: Int) {
        if (amount <= 0) {
            return
        }
        val data = item.silver ?: return
        closeMenu()
        if (!inventory.contains(item.id)) {
            message("You need a ${item.def.name} in order to make a ${itemDefinitions.get(data.item).name}.")
            return
        }
        if (!inventory.contains("silver_bar")) {
            message("You need a silver bar in order to make a ${itemDefinitions.get(data.item).name}.")
            return
        }
        if (!has(Skill.Crafting, data.level)) {
            return
        }
        if (!inventory.contains("silver_bar")) {
            message("You have run out of silver bars to make another ${itemDefinitions.get(data.item).name}.")
            return
        }
        anim("cook_range")
        weakQueue("cast_silver", 3) {
            if (data.amount >= 1) {
                inventory.remove("silver_bar")
                inventory.add(data.item, data.amount)
            } else {
                inventory.replace("silver_bar", data.item)
            }
            exp(Skill.Crafting, data.xp)
            make(item, amount - 1)
        }
    }
}

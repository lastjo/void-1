package content.skill.constitution.drink

import content.entity.effect.toxin.poisoned
import content.entity.player.dialogue.type.item
import content.entity.player.effect.energy.MAX_RUN_ENERGY
import content.entity.player.effect.energy.runEnergy
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.discharge
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.transact.operation.AddCharge.charge
import world.gregs.voidps.engine.inv.transact.operation.RemoveCharge.discharge
import world.gregs.voidps.engine.inv.transact.operation.ReplaceItem.replace
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Consume
import world.gregs.voidps.type.sub.Inventory
import world.gregs.voidps.type.sub.UseOn

class Tea {

    @Consume("cup_of_tea")
    fun tea(player: Player): Boolean {
        player.levels.boost(Skill.Attack, 3)
        return false
    }

    @Consume("guthix_rest_4", "guthix_rest_3", "guthix_rest_2", "guthix_rest_1")
    fun guthixRest(player: Player): Boolean {
        if (player.poisoned) {
            player["poison_damage"] = player["poison_damage", 0] - 10
        }
        player.runEnergy += (MAX_RUN_ENERGY / 100) * 5
        return false
    }

    @Consume("nettle_tea")
    fun nettleTea(player: Player): Boolean {
        player.runEnergy = (MAX_RUN_ENERGY / 100) * 5
        return false
    }

    @Inventory("Look-in", "tea_flask")
    suspend fun lookFlask(player: Player, itemSlot: Int) = player.dialogue {
        val charges = player.inventory.charges(player, itemSlot)
        item(
            "tea_flask",
            400,
            when (charges) {
                0 -> "There's no tea in this flask."
                1 -> "There is one serving of tea in this flask."
                else -> "There is $charges servings of tea in this flask."
            },
        )
    }

    @Consume("tea_flask")
    fun drinkFlask(player: Player, itemSlot: Int): Boolean {
        if (!player.inventory.discharge(player, itemSlot)) {
            player.message("There's nothing left in the flask.")
            return true
        }
        player.say("Ahhh, tea is so refreshing!")
        player.levels.boost(Skill.Attack, 3)
        player.levels.restore(Skill.Constitution, 30)
        player.message("You take a drink from the flask...")
        return true
    }

    @UseOn("tea_flask", "empty_cup")
    fun fillCup(player: Player, toItem: Item, fromSlot: Int, toSlot: Int) {
        val success = player.inventory.transaction {
            discharge(fromSlot, 1)
            replace(toSlot, toItem.id, "cup_of_tea")
        }
        if (success) {
            player.message("You fill the cup with tea.")
        } else {
            player.message("There's nothing left in the flask.")
        }
    }

    @UseOn("cup_of_tea", "tea_flask")
    fun fillFlask(player: Player, fromItem: Item, fromSlot: Int, toSlot: Int) {
        val success = player.inventory.transaction {
            replace(fromSlot, fromItem.id, "empty_cup")
            charge(toSlot, 1)
        }
        if (success) {
            player.message("You add the tea to the flask.")
        } else {
            player.message("The flask is full!")
        }
    }

}

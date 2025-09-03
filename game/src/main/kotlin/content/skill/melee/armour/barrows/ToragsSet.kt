package content.skill.melee.armour.barrows

import content.entity.combat.hit.characterCombatAttack
import content.entity.player.effect.energy.runEnergy
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.playerSpawn
import world.gregs.voidps.engine.inv.itemAdded
import world.gregs.voidps.engine.inv.itemRemoved
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class ToragsSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet()) {
            player["torags_set_effect"] = true
        }
    }

    @ItemAdded("torags_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.hasFullSet()) {
            player["torags_set_effect"] = true
        }
    }

    @ItemRemoved("torags_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun remove(player: Player) {
        player.clear("torags_set_effect")
    }

    @Combat(weapon = "torags_hammers*", type = "melee")
    fun combat(source: Character, target: Character, damage: Int) {
        if (damage <= 0 || target !is Player || !source.contains("torags_set_effect") || random.nextInt(4) != 0) {
            return
        }
        if (target.runEnergy > 0) {
            target.runEnergy -= target.runEnergy / 5
            target.gfx("torags_effect")
        }
    }

    fun Player.hasFullSet() = BarrowsArmour.hasSet(
        this,
        "torags_hammers",
        "torags_helm",
        "torags_platebody",
        "torags_platelegs",
    )
}

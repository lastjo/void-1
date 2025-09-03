package content.skill.melee.armour.barrows

import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.random
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.ItemAdded
import world.gregs.voidps.type.sub.ItemRemoved
import world.gregs.voidps.type.sub.Spawn

class KarilsSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet()) {
            player["karils_set_effect"] = true
        }
    }

    @ItemAdded("karils_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.hasFullSet()) {
            player["karils_set_effect"] = true
        }
    }

    @ItemRemoved("karils_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun remove(player: Player) {
        player.clear("karils_set_effect")
    }

    @Combat(weapon = "karils_crossbow*", type = "range")
    fun combat(source: Character, target: Character, damage: Int) {
        if (damage <= 0 || target !is Player || !source.contains("karils_set_effect") || random.nextInt(4) != 0) {
            return
        }
        if (target.levels.drain(Skill.Agility, multiplier = 0.20) < 0) {
            target.gfx("karils_effect")
        }
    }

    fun Player.hasFullSet() = BarrowsArmour.hasSet(
        this,
        "karils_crossbow",
        "karils_coif",
        "karils_top",
        "karils_skirt",
    )
}

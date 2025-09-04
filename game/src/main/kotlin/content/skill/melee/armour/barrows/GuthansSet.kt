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

class GuthansSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet()) {
            player["guthans_set_effect"] = true
        }
    }

    @ItemAdded("guthans_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.hasFullSet()) {
            player["guthans_set_effect"] = true
        }
    }

    @ItemRemoved("guthans_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun remove(player: Player) {
        player.clear("guthans_set_effect")
    }

    @Combat(type = "melee")
    fun combat(source: Character, target: Character, damage: Int) {
        if (source.contains("guthans_set_effect") && random.nextInt(4) == 0) {
            source.levels.boost(Skill.Constitution, damage)
            target.gfx("guthans_effect")
        }
    }

    fun Player.hasFullSet() = BarrowsArmour.hasSet(
        this,
        "guthans_warspear",
        "guthans_helm",
        "guthans_platebody",
        "guthans_chainskirt",
    )
}

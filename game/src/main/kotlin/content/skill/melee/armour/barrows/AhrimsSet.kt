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

class AhrimsSet {

    @Spawn
    fun spawn(player: Player) {
        if (player.hasFullSet()) {
            player["ahrims_set_effect"] = true
        }
    }

    @ItemRemoved("ahrims_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun removed(player: Player) {
        player.clear("ahrims_set_effect")
    }

    @ItemAdded("ahrims_*", slots = [EquipSlot.HAT, EquipSlot.CHEST, EquipSlot.LEGS, EquipSlot.WEAPON], inventory = "worn_equipment")
    fun added(player: Player) {
        if (player.hasFullSet()) {
            player["ahrims_set_effect"] = true
        }
    }

    @Combat(type = "magic")
    fun combat(source: Character, target: Character, damage: Int) {
        if (damage <= 0) {
            return
        }
        if (!source.contains("ahrims_set_effect") || random.nextInt(4) != 0) {
            return
        }
        val drain = target.levels.drain(Skill.Strength, 5)
        if (drain < 0) {
            target.gfx("ahrims_effect")
        }
    }

    fun Player.hasFullSet() = BarrowsArmour.hasSet(
        this,
        "ahrims_staff",
        "ahrims_hood",
        "ahrims_robe_top",
        "ahrims_robe_skirt",
    )
}

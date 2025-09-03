package content.skill.melee.armour

import content.entity.combat.hit.directHit
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.inv.charges
import world.gregs.voidps.engine.inv.discharge
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.CombatStage
import world.gregs.voidps.type.sub.Combat
import world.gregs.voidps.type.sub.Inventory

class RingOfRecoil {

    @Inventory("Check", "ring_of_recoil", "worn_equipment")
    fun check(player: Player, item: Item) {
        val charges = player.equipment.charges(player, EquipSlot.Ring.index)
        player.message("You can inflict $charges more points of damage before a ring will shatter.")
    }

    @Combat(stage = CombatStage.DAMAGE)
    fun hit(source: Character, player: Player, type: String, damage: Int) {
        if (source == player || type == "deflect" || type == "poison" || type == "disease" || type == "healed" || damage < 1) {
            return
        }
        if (player.equipped(EquipSlot.Ring).id != "ring_of_recoil") {
            return
        }
        if (source is NPC && source.def["immune_deflect", false]) {
            return
        }
        val charges = player.equipment.charges(player, EquipSlot.Ring.index)
        val deflect = (10 + (damage / 10)).coerceAtMost(charges)
        if (player.equipment.discharge(player, EquipSlot.Ring.index, deflect)) {
            source.directHit(deflect, "deflect", source = player)
        }
    }

}

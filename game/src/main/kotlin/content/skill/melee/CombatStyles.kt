package content.skill.melee

import world.gregs.voidps.engine.client.ui.closeInterfaces
import world.gregs.voidps.engine.client.ui.event.interfaceOpen
import world.gregs.voidps.engine.client.ui.event.interfaceRefresh
import world.gregs.voidps.engine.client.ui.interfaceOption
import world.gregs.voidps.engine.data.definition.WeaponStyleDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.npcSpawn
import world.gregs.voidps.engine.inject
import world.gregs.voidps.engine.inv.inventoryChanged
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.*

class CombatStyles(private val styles: WeaponStyleDefinitions) {

    @Spawn
    fun spawn(npc: NPC) {
        npc["combat_style"] = npc.def["style", ""]
    }

    @Open("combat_styles")
    fun open(player: Player) {
        player.sendVariable("attack_style_index")
        player.sendVariable("special_attack_energy")
        player.sendVariable("auto_retaliate")
        refreshStyle(player)
    }

    @Refresh("combat_styles")
    fun refresh(player: Player, id: String) {
        player.interfaceOptions.unlockAll(id, "style1")
        player.interfaceOptions.unlockAll(id, "style2")
        player.interfaceOptions.unlockAll(id, "style3")
        player.interfaceOptions.unlockAll(id, "style4")
    }

    @InventorySlotChanged("worn_equipment", EquipSlot.WEAPON)
    fun change(player: Player) {
        refreshStyle(player)
    }

    @Interface(component = "style*", id = "combat_styles")
    fun select(player: Player, component: String) {
        val index = component.removePrefix("style").toIntOrNull() ?: return
        player.closeInterfaces()
        val type = getWeaponStyleType(player)
        val style = styles.get(type)
        if (index == 1) {
            player.clear("attack_style_${style.stringId}")
        } else {
            player["attack_style_${style.stringId}"] = index - 1
        }
        refreshStyle(player)
    }

    @Interface("Auto Retaliate", "retaliate", "combat_styles")
    fun autoRetaliate(player: Player) {
        player.closeInterfaces()
        player.toggle("auto_retaliate")
    }

    @Interface("Use", "special_attack_bar", "combat_styles")
    fun specialAttack(player: Player) {
        player.toggle("special_attack")
    }

    fun refreshStyle(player: Player) {
        val type = getWeaponStyleType(player)
        val style = styles.get(type)
        val index = player["attack_style_${style.stringId}", 0]
        player["attack_type"] = style.attackTypes.getOrNull(index) ?: ""
        player["attack_style"] = style.attackStyles.getOrNull(index) ?: ""
        player["combat_style"] = style.combatStyles.getOrNull(index) ?: ""
        player["attack_style_index"] = index
    }

    fun getWeaponStyleType(player: Player): Int = player.equipped(EquipSlot.Weapon).def["weapon_style", 0]
}

package content.quest.member.fairy_tale_part_2.fairy_ring

import content.entity.player.modal.Tab
import content.entity.player.modal.tab
import content.quest.quest
import content.quest.questCompleted
import content.skill.magic.spell.Teleport
import content.skill.melee.weapon.weapon
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.client.variable.ListValues
import world.gregs.voidps.engine.data.definition.VariableDefinitions
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.suspend.StringSuspension
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Option

class FairyRing(
    private val variableDefinitions: VariableDefinitions,
    private val fairyCodes: FairyRingCodes,
) {

    @Option("Use", "fairy_ring_*")
    suspend fun useRing(player: Player, target: GameObject) {
        if (player.quest("fairy_tale_ii") == "unstarted") {
            player.message("You don't have permission to use that fairy ring.")
            return
        }
        if (!player.questCompleted("fairy_tale_iii") && player.weapon.id != "dramen_staff") {
            player.message("The fairy ring only works for those who wield fairy magic.")
            return
        }
        player.open("fairy_ring")
        player.open("travel_log")
        val code = StringSuspension.get(player)
        val fairyRing = fairyCodes.codes[code] ?: return
        if (fairyRing.tile == Tile.EMPTY) {
            return
        }
        player.closeMenu()
        player.delay()
        player.walkOverDelay(target.tile)
        player.delay()
        Teleport.teleport(player, fairyRing.tile, "fairy_ring")
        val list: MutableList<String> = player.getOrPut("travel_log_locations") { mutableListOf() }
        list.add(code)
    }

    @Open("fairy_ring")
    fun openUI(player: Player) {
        player.tab(Tab.Inventory)
    }

    @Close("fairy_ring")
    fun closeUI(player: Player) {
        player.open("inventory")
    }

    @Interface("Teleport", "teleport", "fairy_ring")
    fun teleport(player: Player) {
        val code = player.code
        (player.dialogueSuspension as? StringSuspension)?.resume(code)
    }

    @Interface("Rotate clockwise", "clockwise_*", "fairy_ring")
    fun clockwise(player: Player, component: String) {
        val codeIndex = component.removePrefix("clockwise_").toInt()
        rotate(player, codeIndex, 1)
    }

    @Interface("Rotate anticlockwise", "anticlockwise_*", "fairy_ring")
    fun anticlockwise(player: Player, component: String) {
        val codeIndex = component.removePrefix("anticlockwise_").toInt()
        rotate(player, codeIndex, -1)
    }

    val Player.code: String
        get() = "${get("fairy_ring_code_1", "a")}${get("fairy_ring_code_2", "j")}${get("fairy_ring_code_3", "r")}"

    fun rotate(player: Player, codeIndex: Int, amount: Int) {
        val definition = variableDefinitions.get("fairy_ring_code_$codeIndex") ?: return
        val list = definition.values as ListValues
        val current = player["fairy_ring_code_$codeIndex", list.default()]
        val valueIndex = list.values.indexOf(current)
        val next = list.values[(valueIndex + amount) and 3]
        player["fairy_ring_code_$codeIndex"] = next
    }
}

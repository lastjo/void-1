package content.skill.agility.course

import content.entity.sound.sound
import content.quest.questCompleted
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.sub.Option
import world.gregs.voidps.type.sub.Teleport
import world.gregs.voidps.type.sub.TeleportLand

class ApeAtollDungeon {

    @Teleport("Enter", "ape_atoll_hole")
    fun tele(player: Player, target: GameObject): Int {
        val weapon = player.equipped(EquipSlot.Weapon).id
        if (!weapon.endsWith("_greegree")) {
            return -1
        }
        if (weapon == "small_ninja_monkey_greegree") {
            player.message("You scamper through the vine choked hole...")
        } else {
            player.message("Only the stealthiest and most agile monkey can use this!")
            return -1
        }
        return 0
    }

    @TeleportLand("Enter", "ape_atoll_hole")
    fun land(player: Player, target: GameObject) {
        if (player.equipped(EquipSlot.Weapon).id != "small_ninja_monkey_greegree") {
            player.message("You slip climbing down the hole and land hard on the floor.")
            player.anim("stand")
            player.sound("land_flat", delay = 5)
            player.face(Direction.WEST)
        } else {
            player.message("...and find yourself in front of a magnificent Monkey Nut bush.")
        }
    }

    @Option("Pick", "ape_atoll_monkey_nut_bush")
    fun pick(player: Player, target: GameObject) {
        if (player.questCompleted("recipe_for_disaster")) {
            player.message("You have already made the King's meal, you don't need any more of these.")
        }
    }

    @Option("Climb-up", "ape_atoll_hole_exit")
    fun climb(player: Player, target: GameObject) {
        player.message("You climb back out of the cavern.")
    }
}

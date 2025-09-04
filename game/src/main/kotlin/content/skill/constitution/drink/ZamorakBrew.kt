package content.skill.constitution.drink

import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.type.sub.Consume

class ZamorakBrew {

    @Consume("zamorak_brew*", "zamorak_mix*")
    fun zamorak(player: Player): Boolean {
        val health = player.levels.get(Skill.Constitution)
        val damage = ((health / 100) * 10) + 20
        if (health - damage < 0) {
            player.message("You need more hitpoints in order to survive the effects of the zamorak brew.")
            return true
        }
        return false
    }
}

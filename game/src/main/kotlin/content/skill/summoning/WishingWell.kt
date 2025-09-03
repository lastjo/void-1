package content.skill.summoning

import content.entity.npc.shop.openShop
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.type.Script
import world.gregs.voidps.type.sub.Option

class WishingWell {

    @Option("Make-wish", "wishing_well")
    fun wish(player: Player, target: GameObject) {
        player.openShop("summoning_supplies")
    }

}

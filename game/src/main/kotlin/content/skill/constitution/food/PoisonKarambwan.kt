package content.skill.constitution.food

import content.entity.combat.hit.directHit
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.type.sub.Consume

class PoisonKarambwan {

    @Consume("poison_karambwan")
    fun eat(player: Player) {
        player.directHit(50, "poison")
    }

}

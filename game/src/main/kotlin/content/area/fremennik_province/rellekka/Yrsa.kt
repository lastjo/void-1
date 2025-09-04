package content.area.fremennik_province.rellekka

import content.area.asgarnia.falador.openDressingRoom
import content.entity.npc.shop.openShop
import content.entity.player.dialogue.*
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import world.gregs.voidps.engine.client.ui.closeDialogue
import world.gregs.voidps.engine.client.ui.closeMenu
import world.gregs.voidps.engine.client.ui.dialogue.Dialogue
import world.gregs.voidps.engine.data.definition.EnumDefinitions
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.equip.equipped
import world.gregs.voidps.engine.entity.character.player.flagAppearance
import world.gregs.voidps.engine.entity.character.player.sex
import world.gregs.voidps.network.login.protocol.visual.update.player.BodyColour
import world.gregs.voidps.network.login.protocol.visual.update.player.BodyPart
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.sub.Close
import world.gregs.voidps.type.sub.Interface
import world.gregs.voidps.type.sub.Open
import world.gregs.voidps.type.sub.Option

class Yrsa(
    private val enums: EnumDefinitions,
) {

    @Option("Talk-to", "yrsa")
    suspend fun talk(player: Player, npc: NPC) = player.talkWith(npc) {
        npc<Pleased>("Hi. You wanted to buy some clothes? Or did you want to makeover your shoes?")
        choice {
            option<Pleased>("I'd like to buy some clothes.") {
                player.openShop("yrsas_shoe_store")
            }
            option<Pleased>("I'd like to change my shoes.") {
                startShoeShopping()
            }
            option<Talk>("Neither, thanks.") {
                npc<Talk>("As you wish.")
            }
        }
    }

    @Option("Change-shoes", "yrsa")
    suspend fun click(player: Player, npc: NPC) = player.talkWith(npc) {
        startShoeShopping()
    }

    @Close("yrsas_shoe_store")
    fun close(player: Player) {
        player.softTimers.stop("dressing_room")
    }

    @Open("yrsas_shoe_store")
    fun open(player: Player, id: String) {
        player.interfaces.sendText(id, "confirm_text", "Change")
        player.interfaceOptions.unlockAll(id, "styles", 0 until 40)
        val colours = enums.get("colour_shoes")
        player.interfaceOptions.unlockAll(id, "colours", 0 until colours.length * 2)
        player["makeover_shoes"] = player.body.getLook(BodyPart.Feet)
        player["makeover_colour_shoes"] = player.body.getColour(BodyColour.Feet)
    }

    @Interface(component = "styles", id = "yrsas_shoe_store")
    fun styles(player: Player, itemSlot: Int) {
        val value = enums.get("look_shoes_${player.sex}").getInt(itemSlot / 2)
        player["makeover_shoes"] = value
    }

    @Interface(component = "colours", id = "yrsas_shoe_store")
    fun colours(player: Player, itemSlot: Int) {
        player["makeover_colour_shoes"] = enums.get("colour_shoes").getInt(itemSlot / 2)
    }

    @Interface("Confirm", "confirm", "yrsas_shoe_store")
    suspend fun click(player: Player) = player.dialogue {
        player.body.setLook(BodyPart.Feet, player["makeover_shoes", 0])
        player.body.setColour(BodyColour.Feet, player["makeover_colour_shoes", 0])
        player.flagAppearance()
        player.closeMenu()
        npc<Happy>("yrsa", "Hey, They look great!")
    }

    suspend fun Dialogue.startShoeShopping() {
        player.closeDialogue()
        if (player.equipped(EquipSlot.Weapon).isNotEmpty() || player.equipped(EquipSlot.Shield).isNotEmpty()) {
            npc<Afraid>("I don't feel comfortable showing you shoes when you are wielding something. Please remove what you are holding first.")
            return
        }
        if (player.equipped(EquipSlot.Feet).isNotEmpty()) {
            npc<Quiz>("You can't try on shoes with those on your feet.")
            return
        }
        openDressingRoom("yrsas_shoe_store")
    }
}

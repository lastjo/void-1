package content.achievement

import content.entity.combat.killer
import content.entity.npc.shop.shop
import content.skill.melee.weapon.attackStyle
import content.skill.ranged.ammo
import world.gregs.voidps.engine.client.ui.menu
import world.gregs.voidps.engine.data.definition.AreaDefinitions
import world.gregs.voidps.engine.data.definition.WeaponStyleDefinitions
import world.gregs.voidps.engine.entity.character.Character
import world.gregs.voidps.engine.entity.character.move.running
import world.gregs.voidps.engine.entity.character.npc.NPC
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.entity.obj.GameObject
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.entity.obj.ObjectShape
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.network.login.protocol.visual.update.player.EquipSlot
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.sub.*

class LumbridgeBeginnerTasks(
    private val areas: AreaDefinitions,
    private val objects: GameObjects,
    private val styleDefinitions: WeaponStyleDefinitions,
) {

    @ItemAdded("copper_ore")
    fun pick(player: Player) {
        if (player.softTimers.contains("mining") && player.tile in areas["lumbridge_swamp_east_copper_mine"]) {
            player["take_your_pick_task"] = true
        }
    }

    @ItemAdded("logs")
    fun chop(player: Player) {
        if (player.softTimers.contains("woodcutting")) {
            player["adventurers_log_task"] = true
        }
    }

    @ItemAdded("raw_crayfish")
    fun fish(player: Player) {
        if (player.softTimers.contains("fishing")) {
            player["arent_they_supposed_to_be_twins_task"] = true
        }
    }

    @ItemRemoved("logs")
    fun burn(player: Player) {
        if (!player["log_a_rhythm_task", false]) {
            player["burnt_regular_log"] = true
            player["fire_tile"] = player.tile
        }
    }

    @TimerStop("firemaking")
    fun stopFiremaking(player: Player) {
        val regular: Boolean = player.remove("burnt_regular_log") ?: return
        val tile: Tile = player.remove("fire_tile") ?: return
        if (regular) {
            val fire = objects.getShape(tile, ObjectShape.CENTRE_PIECE_STRAIGHT)
            if (fire != null && fire.id.startsWith("fire_")) {
                player["log_a_rhythm_task"] = true
            }
        }
    }

    @ItemRemoved("raw_crayfish")
    fun cook(player: Player, itemSlot: Int) {
        if (player.inventory[itemSlot].id == "crayfish" && player.softTimers.contains("cooking")) {
            player["shellfish_roasting_on_an_open_fire_task"] = true
        }
    }

    @ItemAdded("tin_ore")
    fun mine(player: Player) {
        if (player.softTimers.contains("mining")) {
            player["heavy_metal_task"] = true
        }
    }

    @ItemAdded("bronze_bar")
    fun smelt(player: Player) {
        if (player.softTimers.contains("smelting")) {
            player["bar_one_task"] = true
        }
    }

    @ItemAdded("bronze_dagger")
    fun smith(player: Player) {
        if (player.softTimers.contains("smithing")) {
            player["cutting_edge_technology_task"] = true
        }
    }

    @ItemAdded(inventory = "bank")
    fun banked(player: Player) {
        val millis = System.currentTimeMillis() - player["creation", 0L]
        if (millis > 1000 && !player["hang_on_to_something_task", false]) {
            player["hang_on_to_something_task"] = true
            player.addVarbit("task_reward_items", "magic_staff")
        }
    }

    @ItemRemoved("cowhide")
    fun tan(player: Player, itemSlot: Int) {
        if (player.inventory[itemSlot].id == "leather") {
            player["tan_your_hide_task"] = true
        }
    }

    @ItemAdded("leather_gloves")
    fun craft(player: Player) {
        if (player.softTimers.contains("item_on_item")) {
            player["handicrafts_task"] = true
        }
    }

    @ItemAdded("leather_gloves", slots = [EquipSlot.HANDS], inventory = "worn_equipment")
    fun equipGloves(player: Player) {
        player["handy_dandy_task"] = true
    }

    @ItemAdded("bread")
    fun cook(player: Player) {
        if (player.softTimers.contains("cooking")) {
            player["a_labour_of_loaf_task"] = true
        }
    }

    @ItemAdded("pure_essence", "rune_essence")
    fun essence(player: Player) {
        if (player.softTimers.contains("mining")) {
            player["so_thats_what_ess_stands_for_task"] = true
        }
    }

    @ItemAdded("air_rune")
    fun runecraft(player: Player) {
        if (player.softTimers.contains("runecrafting")) {
            player["air_craft_task"] = true
        }
    }

    @ItemAdded("iron_hatchet")
    fun pickupAxe(player: Player) {
        player["dont_bury_this_one_task"] = true
    }

    @ItemAdded("bronze_mace")
    fun smithMace(player: Player) {
        if (player.softTimers.contains("smithing")) {
            player["mace_invaders_task"] = true
        }
    }

    @ItemAdded("bronze_med_helm")
    fun smithHelm(player: Player) {
        if (player.softTimers.contains("smithing")) {
            player["capital_protection_what_task"] = true
        }
    }

    @ItemAdded("bronze_full_helm")
    fun smithFullHelm(player: Player) {
        if (player.softTimers.contains("smithing")) {
            player["capital_protection_what_task"] = true
        }
    }

    @ItemAdded("empty_pot")
    fun pottery(player: Player) {
        if (player.softTimers.contains("pottery") && player.tile in areas["draynor"]) {
            player["hotpot_task"] = true
        }
    }

    @ItemAdded("raw_shrimps")
    fun shrimp(player: Player) {
        if (player.softTimers.contains("fishing") && player.tile in areas["lumbridge_swamp_fishing_area"]) {
            player["shrimpin_aint_easy_task"] = true
        }
    }

    @ItemAdded("leather_boots")
    fun craftBoots(player: Player) {
        if (player.softTimers.contains("item_on_item")) {
            player["made_for_walking_task"] = true
        }
    }

    @ItemAdded("raw_sardine")
    fun sardine(player: Player) {
        if (player.softTimers.contains("fishing")) {
            player["did_anyone_bring_any_toast_task"] = true
        }
    }

    @ItemRemoved("raw_herring")
    fun cookHerring(player: Player, itemSlot: Int) {
        if (player.inventory[itemSlot].id == "herring" && player.softTimers.contains("cooking")) {
            player["its_not_a_red_one_task"] = true
        }
    }

    @ItemRemoved("uncooked_berry_pie")
    fun cookPie(player: Player, itemSlot: Int) {
        if (player.inventory[itemSlot].id == "redberry_pie" && player.softTimers.contains("cooking")) {
            player["berry_tasty_task"] = true
        }
    }

    @Death("cow*")
    fun cowDeath(cow: NPC) {
        val killer = cow.killer
        if (killer is Player) {
            killer["bovine_intervention_task"] = true
        }
    }

    @Death("giant_rat*")
    fun ratDeath(npc: NPC) {
        val killer = npc.killer
        if (killer is Player && !killer["am_i_a_blademaster_yet_task", false]) {
            when (val style = killer.attackStyle) {
                "aggressive" -> killer["giant_rat_$style"] = true
                "controlled" -> killer["giant_rat_$style"] = true
                "defensive" -> killer["giant_rat_$style"] = true
            }
            if (killer["giant_rat_aggressive", false] && killer["giant_rat_controlled", false] && killer["giant_rat_defensive", false]) {
                killer["am_i_a_blademaster_yet_task"] = true
                killer.clear("giant_rat_aggressive")
                killer.clear("giant_rat_controlled")
                killer.clear("giant_rat_defensive")
            }
        }
    }

    @TeleportLand("Climb-up", "lumbridge_castle_ladder")
    fun climbLadder(player: Player, target: GameObject) {
        player["master_of_all_i_survey_task"] = true
    }

    @Move
    fun playerMove(player: Player) {
        if (player.running && !player["on_the_run_task", false]) {
            player["on_the_run_task"] = true
        }
    }

    @InventorySlotChanged("worn_equipment")
    fun equipped(player: Player, item: Item, itemSlot: Int) {
        when (itemSlot) {
            EquipSlot.Feet.index, EquipSlot.Shield.index, EquipSlot.Legs.index, EquipSlot.Chest.index -> {
                if (item.id.contains("iron")) {
                    player["alls_ferrous_in_love_and_war_task"] = true
                } else if (item.id.contains("steel")) {
                    player["steel_yourself_for_combat_task"] = true
                }
            }
            EquipSlot.Weapon.index -> {
                if (item.id.contains("iron")) {
                    player["not_what_we_mean_by_irony_task"] = true
                } else if (item.id.contains("steel")) {
                    player["temper_temper_task"] = true
                }
                val id = item.def["weapon_style", -1]
                when (val style = styleDefinitions.get(id).stringId) {
                    "staff" -> player["just_cant_get_the_staff_task"] = true
                    "axe", "pickaxe", "dagger", "sword", "2h", "mace", "claws", "hammer", "whip", "spear", "halberd", "ivandis_flail", "salamander" -> {
                        player["armed_and_dangerous_task"] = true
                    }
                    "bow", "crossbow", "thrown", "chinchompa", "sling" -> {
                        player["reach_out_and_touch_someone_task"] = true
                        if (!player["take_a_bow_task", false]) {
                            if (style == "bow") {
                                if (item.id.contains("longbow")) {
                                    player["equip_longbow"] = true
                                } else {
                                    player["equip_shortbow"] = true
                                }
                            } else if (style == "crossbow") {
                                player["equip_crossbow"] = true
                            }
                            if (player["equip_shortbow", false] || player["equip_longbow", false] || player["equip_crossbow", false]) {
                                player["take_a_bow_task"] = true
                                player.clear("equip_shortbow")
                                player.clear("equip_longbow")
                                player.clear("equip_crossbow")
                            }
                        }
                        if (item.id == "oak_shortbow" || item.id == "oak_longbow") {
                            player["heart_of_oak_task"] = true
                        }
                    }
                }
            }
            EquipSlot.Ammo.index -> if (item.id == "iron_arrow") {
                player["ammo_ammo_ammo_task"] = true
            }
        }
    }

    @PrayerStart
    fun startPrayer(player: Player) {
        player["put_your_hands_together_for_task"] = true
    }

    @Combat(type = "range")
    fun rangeAttack(player: Player, character: Character) {
        if (player.ammo == "steel_arrow") {
            player["get_the_point_task"] = true
        }
    }

    @Combat(type = "magic", spell = "confuse")
    fun confuse(player: Player, character: Character) {
        player["not_so_confusing_after_all_task"] = true
    }

    @Combat(type = "magic", spell = "wind_strike")
    fun windStrike(player: Player, character: Character) {
        player["death_from_above_task"] = true
    }

    @Variable("quest_points")
    fun questPoints(player: Player, from: Any?, to: Any?) {
        if ((from == null || from is Int && from < 4) && to != null && to is Int && to >= 4) {
            player["fledgeling_adventurer_task"] = true
        }
    }

    @Variable("task_progress_overall")
    fun overallProgress(player: Player, from: Any?, to: Any?) {
        if ((from == null || from is Int && from < 10) && to is Int && to >= 10) {
            player["on_your_way_task"] = true
        }
    }

    @LevelChange(max = true)
    fun levelUp(player: Player) {
        if (!player["on_the_level_task", false] || !player["quarter_centurion_task", false]) {
            val total = Skill.all.sumOf { (if (it == Skill.Constitution) player.levels.getMax(it) / 10 - 10 else player.levels.getMax(it) - 1) }
            if (total == 10) {
                player["on_the_level_task"] = true
            } else if (total == 25) {
                player["quarter_centurion_task"] = true
            }
        }
    }

    @LevelChange(Skill.ATTACK, Skill.DEFENCE, max = true)
    fun combatLevel(player: Player, from: Int, to: Int) {
        if (from < 5 && to >= 5 && player.levels.getMax(Skill.Attack) >= 5 && player.levels.getMax(Skill.Defence) >= 5) {
            player["first_blood_task"] = true
        }
    }

    @LevelChange(Skill.MINING, max = true)
    fun miningLevel(player: Player, from: Int, to: Int) {
        if (from < 5 && to >= 5) {
            player["hack_and_smash_task"] = true
        }
    }

    @ItemRemoved("raw_shrimps")
    fun sold(player: Player) {
        if (player.menu == "shop" && player.shop() == "lumbridge_fishing_supplies") {
            player["the_fruit_of_the_sea_task"] = true
        }
    }

    @ItemRemoved
    fun soldAnything(player: Player, item: Item) {
        if (player.menu == "shop" && item.id != "coins") {
            player["greasing_the_wheels_of_commerce_task"] = true
        }
    }

    @Subscribe("shop_open", "lumbridge_general_store")
    fun generalStore(player: Player) {
        player["window_shopping_task"] = true
    }

    @Enter("freds_farmhouse")
    fun enterFarm(player: Player) {
        player["wait_thats_not_a_sheep_task"] = true
    }

    @Enter("draynor_manor_courtyard")
    fun enterManor(player: Player) {
        player["in_the_countyard_task"] = true
    }

    @Enter("draynor_village_market")
    fun enterMarket(player: Player) {
        player["beware_of_pigzilla_task"] = true
    }

    @Enter("wizards_tower_top_floor")
    fun enterTower(player: Player) {
        player["tower_power_task"] = true
    }
}

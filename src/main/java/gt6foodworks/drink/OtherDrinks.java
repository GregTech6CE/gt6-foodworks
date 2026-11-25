package gt6foodworks.drink;

import static gt6foodworks.drink.DrinkRegistry.drink;

import gt6foodworks.drink.DrinkRegistry.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.DeferredRegister;

final class OtherDrinks {
    private OtherDrinks() {}

    static void register(DeferredRegister<Item> items) {
        DrinkRegistry.pieces(items, "fruit_remains", 0, 0.0F);
        DrinkRegistry.pieces(items, "vegetable_remains", 0, 0.0F);
        DrinkRegistry.pieces(items, "plant_remains", 0, 0.0F);
        drink(items, "mineral_water", 0xFF92C5DB, 1, 0.1F, UseAnim.DRINK, false, true, false, false,
                new DrinkEffect(MobEffects.REGENERATION, 100, 1, 0.1F));
        drink(items, "soda", 0xFFB7DCED, 1, 0.1F, UseAnim.DRINK, false, true, false, false);
        drink(items, "mineral_soda", 0xFF9AD7E7, 1, 0.2F, UseAnim.DRINK, false, true, false, false,
                new DrinkEffect(MobEffects.REGENERATION, 100, 1, 0.1F));
        drink(items, "sea_water", 0xFF74B8CC, 1, 0.0F, UseAnim.DRINK, false, true, false, false,
                new DrinkEffect(MobEffects.HUNGER, 800, 2, 1.0F));
        drink(items, "ice_water", 0xFFADEAFF, 0, 0.0F, UseAnim.DRINK, false, true, false, false,
                new DrinkEffect(MobEffects.MOVEMENT_SLOWDOWN, 400, 1, 0.4F),
                new DrinkEffect(MobEffects.DIG_SLOWDOWN, 400, 1, 0.4F));
        drink(items, "lemonade", 0xFFECDA79, 4, 0.3F,
                new DrinkEffect(MobEffects.DIG_SPEED, 900, 1, 0.9F));
        drink(items, "alcopops", 0xFFDEBE62, 2, 0.2F,
                new DrinkEffect(MobEffects.DIG_SPEED, 900, 1, 0.9F));
        drink(items, "cactus_water", 0xFF89AB60, 1, 0.1F,
                new DrinkEffect(MobEffects.POISON, 100, 0, 0.9F));
        drink(items, "reedwater", 0xFFADC982, 1, 0.1F);
        drink(items, "potato_juice", 0xFFBFAD75, 3, 0.3F);
        drink(items, "vodka", 0xFFEEEADA, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 500, 1, 0.6F));
        drink(items, "leninade", 0xFFDED8A4, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 500, 2, 0.9F));
        drink(items, "limoncello", 0xFFE0B247, 2, 0.4F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.9F));
        drink(items, "rum", 0xFFEADAB7, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F));
        drink(items, "pirate_brew", 0xFF713A1B, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 2, 0.9F));
        drink(items, "cane_vinegar", 0xFFAE7241, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 2, 0.7F));
        drink(items, "ananas_cider", 0xFFE6AD4E, 4, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 400, 1, 0.6F));
        drink(items, "cave_johnsons_grenade_juice", 0xFFF7C22C, 0, 0.0F, UseAnim.DRINK, false, false, true, false);
        drink(items, "purple_drink", 0xFF764091, 8, 0.2F,
                new DrinkEffect(MobEffects.MOVEMENT_SLOWDOWN, 1200, 2, 0.9F));
        DrinkRegistry.pieces(items, "cactus_pieces", 0, 0.0F);
        DrinkRegistry.pieces(items, "sugar_cane_pieces", 0, 0.0F);
        drink(items, "tea", 0xFFA47238, 2, 0.2F);
        drink(items, "sweet_tea", 0xFFB88A44, 2, 0.2F);
        drink(items, "iced_tea", 0xFFD6A361, 6, 0.4F);
        drink(items, "dark_coffee", 0xFF492B24, 2, 0.2F);
        drink(items, "coffee", 0xFF946144, 4, 0.4F);
        drink(items, "cafe_au_lait", 0xFFAD8264, 4, 0.4F);
        drink(items, "lait_au_cafe", 0xFFCFAF8D, 4, 0.4F);
        drink(items, "dark_cafe_au_lait", 0xFF724533, 2, 0.2F);
        drink(items, "dark_chocolate_milk", 0xFF654032, 4, 0.4F);
        drink(items, "chocolate_milk", 0xFF967058, 4, 0.4F);
        drink(items, "chocolate_cream", 0xFFAD8660, 4, 0.2F, UseAnim.EAT, false, false, false, false);
        drink(items, "nutella", 0xFF895238, 8, 0.4F, UseAnim.EAT, false, false, false, false);
        drink(items, "golden_carrot_juice", 0xFFFCC957, 4, 0.2F, UseAnim.DRINK, false, false, false, true,
                new DrinkEffect(MobEffects.NIGHT_VISION, 1200, 0, 0.9F));
        DrinkRegistry.pieces(items, "golden_carrot_pieces", 0, 0.0F);
        drink(items, "golden_apple_juice", 0xFFF6C63D, 4, 0.2F, UseAnim.DRINK, false, false, false, true,
                new DrinkEffect(MobEffects.ABSORPTION, 2400, 0, 1.0F),
                new DrinkEffect(MobEffects.REGENERATION, 100, 1, 1.0F));
        DrinkRegistry.pieces(items, "golden_apple_pieces", 0, 0.0F);
        drink(items, "iduns_apple_juice", 0xFFFFCB4F, 4, 0.2F, UseAnim.DRINK, false, false, false, true,
                new DrinkEffect(MobEffects.REGENERATION, 600, 4, 1.0F),
                new DrinkEffect(MobEffects.ABSORPTION, 2400, 0, 1.0F),
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 6000, 0, 1.0F),
                new DrinkEffect(MobEffects.FIRE_RESISTANCE, 6000, 0, 1.0F));
        DrinkRegistry.pieces(items, "enchanted_golden_apple_pieces", 0, 0.0F);
        drink(items, "golden_cider", 0xFFDDA33D, 4, 0.2F, UseAnim.DRINK, false, false, false, true,
                new DrinkEffect(MobEffects.ABSORPTION, 2400, 1, 0.95F));
        drink(items, "notches_brew", 0xFFDDA33D, 4, 0.2F, UseAnim.DRINK, false, false, false, true,
                new DrinkEffect(MobEffects.REGENERATION, 700, 4, 0.95F),
                new DrinkEffect(MobEffects.ABSORPTION, 3000, 1, 0.95F),
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 7000, 1, 0.95F),
                new DrinkEffect(MobEffects.FIRE_RESISTANCE, 7000, 0, 0.95F));
        drink(items, "grape_juice", 0xFFCCA94A, 4, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_juice_2", 0xFFAC424B, 4, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_juice_3", 0xFF91AB4C, 4, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_juice_4", 0xFF74446F, 4, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_smoothie", 0xFFEAD28A, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_smoothie_2", 0xFFD77174, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_smoothie_3", 0xFFC0CF75, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "grape_smoothie_4", 0xFFB176A3, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F));
        drink(items, "wine", 0xFFA6A757, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "ricardo_sanchez", 0xFF7A3E57, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "white_wine", 0xFFC3A856, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "red_wine", 0xFF913949, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "grape_vinegar", 0xFFA36B53, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 1, 0.7F));
        drink(items, "currant_juice", 0xFF8D3656, 3, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "currant_smoothie", 0xFFB7748E, 2, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "cherry_juice", 0xFFA43143, 6, 0.4F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.1F));
        drink(items, "cherry_smoothie", 0xFFDB6D77, 4, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.1F));
        drink(items, "kiwi_juice", 0xFF7D9F45, 4, 0.4F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        drink(items, "kiwi_smoothie", 0xFFABC866, 3, 0.2F,
                new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F));
        drink(items, "coconut_milk", 0xFFF5EEE1, 6, 0.8F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "coconut_cream", 0xFFF3DECA, 2, 0.4F);
        drink(items, "coconut_smoothie", 0xFFFBF4E2, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F));
        drink(items, "pi_a_colada", 0xFFF5DEA0, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F));
        drink(items, "banana_wine", 0xFFD7B761, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "carrot_wine", 0xFFCC813C, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "cranberry_wine", 0xFF9B3C4D, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "tomato_wine", 0xFFC2553B, 2, 0.2F,
                new DrinkEffect(MobEffects.HEAL, 1, 0, 0.6F));
        drink(items, "maple_sap", 0xFFDFBC7A, 3, 0.2F,
                new DrinkEffect(MobEffects.HUNGER, 300, 1, 0.5F));
        drink(items, "maple_syrup", 0xFFA76331, 4, 0.2F);
        drink(items, "rainbow_sap", 0xFFD7ACE7, 5, 0.5F, UseAnim.DRINK, true, false, false, false,
                new DrinkEffect(MobEffects.REGENERATION, 60, 4, 1.0F),
                new DrinkEffect(MobEffects.DIG_SPEED, 900, 2, 1.0F));
        drink(items, "honeydew", 0xFFCFB570, 2, 0.2F, UseAnim.DRINK, true, false, false, false,
                new DrinkEffect(MobEffects.MOVEMENT_SPEED, 600, 0, 1.0F));
        drink(items, "royal_jelly", 0xFFE7C482, 2, 0.2F, UseAnim.DRINK, true, false, false, false,
                new DrinkEffect(MobEffects.REGENERATION, 150, 1, 1.0F));
        drink(items, "ambrosia", 0xFFF7CD57, 2, 0.2F, UseAnim.DRINK, true, false, false, false,
                new DrinkEffect(MobEffects.REGENERATION, 150, 0, 1.0F));
        drink(items, "short_mead", 0xFFD0A144, 3, 0.1F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F),
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 300, 1, 0.6F));
        drink(items, "mead", 0xFFB97B34, 3, 0.1F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F),
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 300, 1, 0.6F));
    }
}

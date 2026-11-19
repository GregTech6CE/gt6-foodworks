package gregtechfood;

import static gregtechfood.FruitDrinks.drink;
import static gregtechfood.FruitDrinks.fluid;

import gregtechfood.FruitDrinks.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

final class SpiritDrinks {
    private SpiritDrinks() {}

    static void register(DeferredRegister<Item> items) {
        var brandyEffect = new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 400, 1, 0.6F);
        drink(items, "apple_brandy", 0xFFB16B32, 2, 0.1F, brandyEffect);
        drink(items, "apricot_brandy", 0xFFC17D29, 2, 0.1F, brandyEffect);
        drink(items, "cherry_brandy", 0xFF934759, 2, 0.1F, brandyEffect);
        drink(items, "citrus_brandy", 0xFFCE922D, 2, 0.1F, brandyEffect);
        drink(items, "elderberry_brandy", 0xFF62415E, 2, 0.1F, brandyEffect);
        drink(items, "fruit_brandy", 0xFFC37250, 2, 0.1F, brandyEffect);
        drink(items, "grape_brandy", 0xFF914E41, 2, 0.1F, brandyEffect);
        drink(items, "pear_brandy", 0xFFA99341, 2, 0.1F, brandyEffect);
        drink(items, "plum_brandy", 0xFF854850, 2, 0.1F, brandyEffect);

        var spiritEffect = new DrinkEffect(MobEffects.DAMAGE_BOOST, 500, 1, 0.6F);
        drink(items, "apple_liquor", 0xFFD9B873, 2, 0.2F, spiritEffect);
        drink(items, "apricot_liquor", 0xFFEEB573, 2, 0.2F, spiritEffect);
        drink(items, "cherry_liquor", 0xFFC48391, 2, 0.2F, spiritEffect);
        drink(items, "elderberry_liquor", 0xFFAE8AB0, 2, 0.2F, spiritEffect);
        drink(items, "fruit_liquor", 0xFFDC9883, 2, 0.2F, spiritEffect);
        drink(items, "pear_liquor", 0xFFD2C885, 2, 0.2F, spiritEffect);
        drink(items, "gin", 0xFFD1E9E0, 2, 0.2F, spiritEffect);
        drink(items, "neutral_spirit", 0xFFE4EDF2, 2, 0.2F, spiritEffect);
        drink(items, "sugar_cane_spirit", 0xFFE5DDBD, 2, 0.2F, spiritEffect);
        drink(items, "tequila", 0xFFE9E2AA, 2, 0.2F, spiritEffect);

        fluid("citrus_must", 0xFFDCAD46);
        fluid("fruit_must", 0xFFDF8A57);
        fluid("apricot_wine", 0xFFE6A34F);
        fluid("cherry_wine", 0xFF862C4D);
        fluid("citrus_wine", 0xFFCC9F40);
        fluid("elderberry_wine", 0xFF532B5E);
        fluid("fruit_wine", 0xFFC47755);
        fluid("pear_wine", 0xFFC2AA63);
        fluid("plum_wine", 0xFF714057);
        fluid("agave_wine", 0xFFB6B36F);
    }
}

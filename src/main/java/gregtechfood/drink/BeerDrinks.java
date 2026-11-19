package gregtechfood.drink;

import gregtechfood.drink.DrinkRegistry.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

final class BeerDrinks {
    private BeerDrinks() {}

    static void register(DeferredRegister<Item> items) {
        DrinkRegistry.pieces(items, "barley_malt", 0, 0);
        DrinkRegistry.pieces(items, "roasted_barley_malt", 0, 0);
        DrinkRegistry.pieces(items, "brewing_yeast", 0, 0);
        DrinkRegistry.pieces(items, "lager_yeast", 0, 0);

        DrinkRegistry.fluid("barley_wort", 0xFFBA7934);
        DrinkRegistry.fluid("corn_wort", 0xFFD69B49);
        DrinkRegistry.fluid("rye_wort", 0xFF996736);
        DrinkRegistry.fluid("roasted_barley_wort", 0xFF58372A);
        DrinkRegistry.fluid("wheat_wort", 0xFFC58B40);

        var haste = new DrinkEffect(MobEffects.DIG_SPEED, 400, 2, 0.6F);
        DrinkRegistry.drink(items, "ale", 0xFFC17A2D, 6, 0.4F, haste);
        DrinkRegistry.drink(items, "corn_beer", 0xFFFFC64F, 6, 0.4F, haste);
        DrinkRegistry.drink(items, "lager", 0xFFF1B344, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F));
        DrinkRegistry.drink(items, "rye_beer", 0xFFB15F2D, 6, 0.4F, haste);
        DrinkRegistry.drink(items, "stout", 0xFF50362B, 6, 0.4F, haste);
        DrinkRegistry.drink(items, "wheat_beer", 0xFFE4A746, 6, 0.4F, haste);
    }
}

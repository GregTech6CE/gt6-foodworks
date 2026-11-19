package gregtechfood;

import static gregtechfood.FruitDrinks.drink;

import gregtechfood.FruitDrinks.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.registries.DeferredRegister;

final class BrewingAndCondiments {
    private BrewingAndCondiments() {}

    static void register(DeferredRegister<Item> items) {
        FruitDrinks.fluid("rice_mash", 0xFFDDD2AC);

        var strength = new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 1, 0.6F);
        drink(items, "hops_mash", 0xFF9DA94B, 1, 0.1F);
        drink(items, "dark_beer", 0xFF60362A, 4, 0.4F, strength);
        drink(items, "dragon_blood", 0xFF93352A, 4, 0.4F,
                new DrinkEffect(MobEffects.DAMAGE_BOOST, 300, 2, 0.9F));
        drink(items, "sake", 0xFFE9E4D5, 4, 0.4F, strength);
        drink(items, "dressing", 0xFFE4C97D, 1, 0.5F);
        FruitDrinks.registerBottle(items, "soy_milk", 0xFFF1E4BC, () -> new BottledDrinkItem(
                new FoodProperties.Builder().alwaysEat().build(), UseAnim.DRINK, true));
    }
}

package gregtechfood;

import gregtechfood.FruitDrinks.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

final class RemainingFruitDrinks {
    private RemainingFruitDrinks() {}

    static void register(DeferredRegister<Item> items) {
        var haste = new DrinkEffect(MobEffects.DIG_SPEED, 1200, 0, 0.6F);
        var hunger = new DrinkEffect(MobEffects.HUNGER, 100, 1, 0.2F);
        var berryStrength = new DrinkEffect(MobEffects.DAMAGE_BOOST, 200, 1, 0.25F);
        var pearHunger = new DrinkEffect(MobEffects.HUNGER, 200, 0, 0.2F);
        var grapefruitHunger = new DrinkEffect(MobEffects.HUNGER, 200, 1, 0.6F);

        FruitDrinks.drink(items, "apricot_juice", 0xFFEBA159, 3, 0.8F, haste);
        FruitDrinks.drink(items, "apricot_smoothie", 0xFFF6B87C, 2, 0.4F, haste);
        FruitDrinks.drink(items, "orange_juice", 0xFFE6A144, 3, 0.8F, haste);
        FruitDrinks.drink(items, "orange_smoothie", 0xFFFFBF61, 2, 0.4F, haste);
        FruitDrinks.drink(items, "elderberry_juice", 0xFF5A345F, 3, 0.4F, berryStrength);
        FruitDrinks.drink(items, "elderberry_smoothie", 0xFF925783, 2, 0.2F, berryStrength);
        FruitDrinks.drink(items, "peach_juice", 0xFFF0AE76, 2, 0.4F, hunger);
        FruitDrinks.drink(items, "peach_smoothie", 0xFFF5C6AC, 1, 0.2F, hunger);
        FruitDrinks.drink(items, "pear_juice", 0xFFDCC77A, 6, 0.4F, pearHunger);
        FruitDrinks.drink(items, "pear_smoothie", 0xFFE3D99D, 4, 0.2F, pearHunger);
        FruitDrinks.drink(items, "plum_juice", 0xFF87465F, 2, 0.4F, hunger);
        FruitDrinks.drink(items, "plum_smoothie", 0xFFB37296, 1, 0.2F, hunger);
        FruitDrinks.drink(items, "lime_juice", 0xFF90B957, 3, 0.8F, haste);
        FruitDrinks.drink(items, "lime_smoothie", 0xFFB6D573, 2, 0.4F, haste);
        FruitDrinks.drink(items, "grapefruit_juice", 0xFFE58575, 3, 0.8F, grapefruitHunger);
        FruitDrinks.drink(items, "grapefruit_smoothie", 0xFFF7B2A8, 2, 0.4F, grapefruitHunger);
        FruitDrinks.drink(items, "persimmon_juice", 0xFFDC7732, 3, 0.8F, haste);
        FruitDrinks.drink(items, "persimmon_smoothie", 0xFFEF9F54, 2, 0.4F, haste);
        FruitDrinks.drink(items, "starfruit_juice", 0xFFC8BF56, 3, 0.4F, hunger);
        FruitDrinks.drink(items, "starfruit_smoothie", 0xFFE1D972, 2, 0.2F, hunger);
        FruitDrinks.drink(items, "fig_juice", 0xFF916D8B, 2, 0.4F, hunger);
        FruitDrinks.drink(items, "fig_smoothie", 0xFFC194B6, 1, 0.2F, hunger);
        FruitDrinks.drink(items, "mango_juice", 0xFFF0B03D, 3, 0.4F, hunger);
        FruitDrinks.drink(items, "mango_smoothie", 0xFFF9CB67, 2, 0.2F, hunger);
        FruitDrinks.drink(items, "papaya_juice", 0xFFEE8A45, 3, 0.4F, hunger);
        FruitDrinks.drink(items, "papaya_smoothie", 0xFFF7B57E, 2, 0.2F, hunger);
        FruitDrinks.drink(items, "juice", 0xFFCB7652, 3, 0.4F, hunger);
        FruitDrinks.drink(items, "froot_smoothie", 0xFFE99E83, 2, 0.2F, hunger);
    }
}

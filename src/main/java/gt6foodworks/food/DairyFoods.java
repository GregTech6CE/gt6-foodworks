package gt6foodworks.food;

import gt6foodworks.drink.BottledFoodItem;
import gt6foodworks.drink.DrinkRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

final class DairyFoods {
    private static final List<RegistryObject<Item>> FOODS = new ArrayList<>();

    private DairyFoods() {}

    static void register(DeferredRegister<Item> items) {
        DrinkRegistry.registerBottle(items, "milk", 0xFFF4F1E8, () -> new BottledFoodItem(
                new FoodProperties.Builder().alwaysEat().build(), UseAnim.DRINK, true));
        DrinkRegistry.registerBottle(items, "heavy_cream", 0xFFEFE1B8, () -> new BottledFoodItem(
                new FoodProperties.Builder().nutrition(2).saturationMod(0.4F).build()));
        DrinkRegistry.registerBottle(items, "milk_2", 0xFFC4BE9A, () -> new BottledFoodItem(
                new FoodProperties.Builder().alwaysEat()
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 100, 1), 1.0F).build()));

        food(items, "butter", 1, 4.0F);
        food(items, "salted_butter", 1, 4.0F);
        food(items, "cheese", 2, 1.2F);
        food(items, "cheese_slice", 1, 0.6F);
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        FOODS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        FOODS.add(items.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build()))));
    }
}

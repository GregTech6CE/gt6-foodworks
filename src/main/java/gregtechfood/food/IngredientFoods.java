package gregtechfood.food;

import gregtechfood.drink.BottledFoodItem;
import gregtechfood.drink.DrinkRegistry;
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

final class IngredientFoods {
    private static final List<RegistryObject<Item>> ITEMS = new ArrayList<>();

    private IngredientFoods() {}

    static void register(DeferredRegister<Item> items) {
        food(items, "green_raisins", 2, 0.6F);
        food(items, "white_raisins", 2, 0.6F);
        food(items, "red_raisins", 2, 0.6F);
        food(items, "purple_raisins", 2, 0.6F);
        food(items, "pomeraisins", 2, 0.6F);
        food(items, "chocolate_raisins", 3, 1.2F);
        food(items, "apple_slice", 1, 0.4F);
        food(items, "apple_slice_2", 1, 0.3F);
        food(items, "apple_slice_3", 1, 0.3F);
        food(items, "apple_slice_4", 1, 0.4F);
        food(items, "pickle", 1, 1.2F);
        food(items, "pickle_slice", 0, 0.3F);
        food(items, "carrot_slice", 0, 0.3F);
        food(items, "caramel", 1, 0.2F);
        food(items, "cinnamon_bark", 2, 0.3F);
        ITEMS.add(items.register("maxim_tomato", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(9).saturationMod(1F)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 60, 4), 1F).build()))));
        for (String id : List.of("apple_core", "apple_core_2", "apple_core_3", "apple_core_4", "ground_black_pepper",
                "ground_pistachio", "ground_vanilla", "nut_remains")) {
            ITEMS.add(items.register(id, () -> new Item(new Item.Properties())));
        }

        DrinkRegistry.registerBottle(items, "barbecue_sauce", 0xFF873721,
                () -> new BottledFoodItem(new FoodProperties.Builder().nutrition(4).saturationMod(0.3F)
                        .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1), 0.1F)
                        .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200), 0.2F).build()));
        DrinkRegistry.registerBottle(items, "peanut_butter", 0xFFB77E42,
                () -> new BottledFoodItem(new FoodProperties.Builder().nutrition(8).saturationMod(0.4F).build(),
                        UseAnim.EAT, false));
        hotSauce(items, "chili_sauce", 0xFFD94624, 1000, 0, 0.1F, 0.6F);
        hotSauce(items, "hot_sauce", 0xFFD32B1C, 2000, 0, 0.3F, 0.7F);
        hotSauce(items, "diabolo_sauce", 0xFFC71F24, 3000, 1, 0.5F, 0.8F);
        hotSauce(items, "diablo_sauce", 0xFFA41B31, 4000, 1, 0.7F, 0.9F);
        hotSauce(items, "there_is_no_cow_sauce", 0xFFF83E22, 9999, 2, 1.0F, 1.0F);
    }

    private static void hotSauce(DeferredRegister<Item> items, String id, int tint, int duration,
                                 int nauseaAmplifier, float nauseaChance, float resistanceChance) {
        DrinkRegistry.registerBottle(items, id, tint,
                () -> new BottledFoodItem(new FoodProperties.Builder().nutrition(2).saturationMod(0.1F)
                        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, duration, nauseaAmplifier), nauseaChance)
                        .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration), resistanceChance)
                        .build()));
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        ITEMS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        ITEMS.add(items.register(id, () -> new Item(foodProperties(nutrition, saturation))));
    }

    private static Item.Properties foodProperties(int nutrition, float saturation) {
        return new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition).saturationMod(saturation).build());
    }
}

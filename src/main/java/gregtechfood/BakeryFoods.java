package gregtechfood;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

final class BakeryFoods {
    private static final List<RegistryObject<Item>> CREATIVE_ITEMS = new ArrayList<>();

    private BakeryFoods() {}

    static void register(DeferredRegister<Item> items) {
        food(items, "cookie_shaped_dough", 1, 0.2F);
        food(items, "cookie_shaped_raisin_dough", 1, 0.2F);
        food(items, "raisin_cookie", 2, 0.2F);
        food(items, "cookie_shaped_chocolate_raisin_dough", 1, 0.2F);
        food(items, "cookie", 2, 0.2F);
        food(items, "raw_cake_bottom", 2, 0.2F);
        food(items, "cake_bottom", 3, 0.2F);
        food(items, "flattened_dough", 1, 0.2F);
        food(items, "flat_dough_with_ketchup", 2, 0.2F);
        food(items, "raw_pizza_margherita", 2, 0.3F);
        food(items, "pizza_margherita", 6, 1.2F);
        food(items, "raw_mince_meat_pizza", 2, 0.3F);
        food(items, "mince_meat_pizza", 7, 1.2F);
        food(items, "raw_veggie_pizza", 1, 0.3F);
        food(items, "veggie_pizza", 5, 1.2F);
        food(items, "raw_pizza_hawaii", 2, 0.3F);
        food(items, "pizza_hawaii", 7, 1.2F);
        food(items, "dough_bun", 1, 0.6F);
        food(items, "bun", 2, 1.2F);
        food(items, "sliced_bun", 1, 1.2F);
        food(items, "buns", 2, 1.2F);
        food(items, "dough_bread", 1, 0.6F);
        food(items, "sliced_bread", 2, 1.2F);
        food(items, "breads", 5, 1.2F);
        food(items, "dough_baguette", 1, 0.6F);
        food(items, "baguette", 8, 1.2F);
        food(items, "sliced_baguette", 4, 1.2F);
        food(items, "baguettes", 8, 1.2F);
        food(items, "flattened_egg_dough", 1, 0.6F);
        food(items, "dough_toast_loaf", 1, 0.6F);
        food(items, "loaf_of_toast", 8, 1.2F);
        food(items, "toast", 1, 1.2F);
        food(items, "toasted_toast", 1, 1.2F);
        food(items, "dough", 1, 1F);
        food(items, "sugary_dough", 1, 1F);
        food(items, "chocolate_dough", 1, 1F);
        food(items, "egg_dough", 1, 1F);
        food(items, "sugary_raisin_dough", 1, 1F);
        food(items, "sugary_chocolate_raisin_dough", 1, 1F);
        MinecraftForge.EVENT_BUS.addListener(BakeryFoods::remapRemovedDough);
    }

    private static void remapRemovedDough(MissingMappingsEvent event) {
        for (var mapping : event.getMappings(Registries.ITEM, GregTechFood.MOD_ID)) {
            String replacement = switch (mapping.getKey().getPath()) {
                case "abyssal_dough" -> "dough";
                case "cookie_shaped_abyssal_dough" -> "cookie_shaped_dough";
                default -> null;
            };
            if (replacement != null) {
                var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(
                        GregTechFood.MOD_ID, replacement));
                if (item != null) mapping.remap(item);
            }
        }
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_ITEMS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        CREATIVE_ITEMS.add(items.register(id, () -> {
            var properties = new Item.Properties();
            if (nutrition > 0) {
                properties.food(new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build());
            }
            return new Item(properties);
        }));
    }
}

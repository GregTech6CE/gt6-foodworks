package gregtechfood.food;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

final class MeatFoods {
    private static final List<RegistryObject<Item>> MEATS = new ArrayList<>();

    private MeatFoods() {}

    static void register(DeferredRegister<Item> items) {
        meat(items, "raw_ham", 3, 0.6F);
        meat(items, "cooked_ham", 10, 1.6F);
        meat(items, "raw_ham_slice", 1, 0.6F);
        meat(items, "cooked_ham_slice", 3, 1.6F);
        meat(items, "raw_bacon", 1, 0.9F);
        meat(items, "grilled_bacon", 3, 1.8F);
        meat(items, "raw_ribs", 3, 0.6F);
        meat(items, "grilled_ribs", 10, 1.6F);
        meat(items, "raw_rib_eye_steak", 3, 0.6F);
        meat(items, "grilled_rib_eye_steak", 10, 1.6F);
        meat(items, "dogmeat", 2, 0.6F);
        meat(items, "grilled_dogmeat", 8, 1.6F);
        meat(items, "horse_meat", 2, 0.6F);
        meat(items, "grilled_horse_meat", 8, 1.6F);
        meat(items, "mule_meat", 3, 0.8F);
        meat(items, "grilled_mule_meat", 10, 1.8F);
        meat(items, "donkey_meat", 2, 0.6F);
        meat(items, "grilled_donkey_meat", 8, 1.6F);
        meat(items, "barbecue_ribs", 10, 1.6F);
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        MEATS.forEach(event::accept);
    }

    private static void meat(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        MEATS.add(items.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build()))));
    }
}

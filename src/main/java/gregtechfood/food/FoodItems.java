package gregtechfood.food;

import gregtechfood.crop.FoodCrops;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public final class FoodItems {
    private FoodItems() {}

    public static void register(IEventBus bus, DeferredRegister<Item> items) {
        PotatoFoods.register(items);
        CannedFoods.register(items);
        BakeryFoods.register(items);
        MeatFoods.register(items);
        IceCreamFoods.register(items);
        EggFoods.register(items);
        DairyFoods.register(items);
        FoodCrops.register(bus, items);
        IngredientFoods.register(items);
        HandheldFoods.register(items);
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        PotatoFoods.addCreativeItems(event);
        CannedFoods.addCreativeItems(event);
        BakeryFoods.addCreativeItems(event);
        MeatFoods.addCreativeItems(event);
        IceCreamFoods.addCreativeItems(event);
        EggFoods.addCreativeItems(event);
        DairyFoods.addCreativeItems(event);
        IngredientFoods.addCreativeItems(event);
        HandheldFoods.addCreativeItems(event);
    }
}

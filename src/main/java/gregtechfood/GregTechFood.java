package gregtechfood;

import gregtechfood.crop.FoodCrops;
import gregtechfood.drink.DrinkRegistry;
import gregtechfood.drink.SpecialConsumables;
import gregtechfood.food.FoodItems;
import gregtechfood.loot.FoodLootModifiers;
import gregtechfood.sandwich.SandwichBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(GregTechFood.MOD_ID)
public final class GregTechFood {
    public static final String MOD_ID = "gt6food";

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public GregTechFood(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        DrinkRegistry.register(bus, ITEMS);
        FoodItems.register(bus, ITEMS);
        SandwichBlock.register(bus, ITEMS);
        FoodLootModifiers.register(bus);
        ITEMS.register(bus);
        bus.addListener(GregTechFood::addCreativeItems);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        FoodCrops.addCreativeItems(event);
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            DrinkRegistry.addCreativeItems(event);
            FoodItems.addCreativeItems(event);
            SandwichBlock.addCreativeItems(event);
            SpecialConsumables.addCreativeItems(event);
        }
    }
}

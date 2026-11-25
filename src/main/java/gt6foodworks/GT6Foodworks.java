package gt6foodworks;

import gt6foodworks.crop.FoodCrops;
import gt6foodworks.drink.DrinkRegistry;
import gt6foodworks.drink.SpecialConsumables;
import gt6foodworks.food.FoodItems;
import gt6foodworks.loot.FoodLootModifiers;
import gt6foodworks.sandwich.SandwichBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(GT6Foodworks.MOD_ID)
public final class GT6Foodworks {
    public static final String MOD_ID = "gt6food";

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public GT6Foodworks(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        DrinkRegistry.register(bus, ITEMS);
        FoodItems.register(bus, ITEMS);
        SandwichBlock.register(bus, ITEMS);
        FoodLootModifiers.register(bus);
        ITEMS.register(bus);
        bus.addListener(GT6Foodworks::addCreativeItems);
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

package gregtechfood;

import com.mojang.serialization.Codec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(GregTechFood.MOD_ID)
public final class GregTechFood {
    public static final String MOD_ID = "gt6food";

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    private static final RegistryObject<Item> APPLE_PIECES = ITEMS.register(
            "apple_pieces", () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build())));

    public GregTechFood(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        FruitDrinks.register(bus, ITEMS);
        PotatoFoods.register(ITEMS);
        CannedFoods.register(ITEMS);
        BakeryFoods.register(ITEMS);
        MeatFoods.register(ITEMS);
        IceCreamFoods.register(ITEMS);
        EggFoods.register(ITEMS);
        DairyFoods.register(ITEMS);
        FoodCrops.register(bus, ITEMS);
        IngredientFoods.register(ITEMS);
        HandheldFoods.register(ITEMS);
        PlaceableSandwich.register(bus, ITEMS);
        LOOT_MODIFIERS.register("extra_loot", () -> ExtraLootModifier.CODEC);
        LOOT_MODIFIERS.register("migrated_food", () -> MigratedFoodLoot.CODEC);
        LOOT_MODIFIERS.register(bus);
        ITEMS.register(bus);
        bus.addListener(GregTechFood::addCreativeItems);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        FoodCrops.addCreativeItems(event);
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(APPLE_PIECES);
            FruitDrinks.addCreativeItems(event);
            PotatoFoods.addCreativeItems(event);
            CannedFoods.addCreativeItems(event);
            BakeryFoods.addCreativeItems(event);
            MeatFoods.addCreativeItems(event);
            IceCreamFoods.addCreativeItems(event);
            EggFoods.addCreativeItems(event);
            DairyFoods.addCreativeItems(event);
            IngredientFoods.addCreativeItems(event);
            HandheldFoods.addCreativeItems(event);
            PlaceableSandwich.addCreativeItems(event);
            SpecialFoods.addCreativeItems(event);
        }
    }
}

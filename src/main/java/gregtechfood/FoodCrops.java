package gregtechfood;

import static gregtechfood.GregTechFood.MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

final class FoodCrops {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final List<Crop> CROPS = new ArrayList<>();
    private static final List<RegistryObject<Item>> SLICES = new ArrayList<>();

    private FoodCrops() {}

    static void register(IEventBus bus, DeferredRegister<Item> items) {
        add(items, "tomato", 1, 0.6F);
        add(items, "onion", 1, 1.2F);
        add(items, "cucumber", 1, 1.2F);
        add(items, "blackberry", 1, 0.6F);
        add(items, "blueberry", 1, 0.6F);
        add(items, "raspberry", 1, 0.6F);
        add(items, "cranberry", 1, 0.6F);
        add(items, "gooseberry", 1, 0.6F);
        add(items, "strawberry", 1, 0.6F);
        add(items, "ananas", 4, 0.3F);
        add(items, "lemon", 1, 0.6F);
        add(items, "pomegranate", 1, 0.6F);
        add(items, "banana", 1, 0.6F);
        add(items, "green_grapes", 1, 0.6F);
        add(items, "white_grapes", 1, 0.6F);
        add(items, "red_grapes", 1, 0.6F);
        add(items, "purple_grapes", 1, 0.6F);
        add(items, "black_currants", 1, 0.6F);
        add(items, "white_currants", 1, 0.6F);
        add(items, "red_currants", 1, 0.6F);
        add(items, "chili_pepper", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(1).saturationMod(1.2F)
                .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 1), 0.4F).build())));
        add(items, "black_pepper", () -> new Item(new Item.Properties()));
        add(items, "coconut", 2, 0.3F);
        add(items, "hazelnut", 2, 0.3F);
        add(items, "pistachio", 2, 0.3F);
        add(items, "cherry", 1, 0.6F);
        add(items, "kiwi", 1, 0.6F);
        add(items, "tea_leaves", () -> new Item(new Item.Properties()));
        add(items, "coffee_beans", () -> new Item(new Item.Properties()));
        add(items, "mint_leaves", () -> new Item(new Item.Properties()));
        add(items, "vanilla_bean", () -> new Item(new Item.Properties()));
        add(items, "peanut", 2, 0.3F);
        add(items, "candleberry", 1, 0.6F);
        add(items, "apple", () -> IngredientFoods.apple("apple_core", 4, 0.4F));
        add(items, "apple_2", () -> IngredientFoods.apple("apple_core_2", 5, 0.3F));
        add(items, "apple_3", () -> IngredientFoods.apple("apple_core_4", 5, 0.4F));
        add(items, "apricot", 2, 0.3F);
        add(items, "orange", 2, 0.3F);
        add(items, "elderberry", 1, 0.6F);
        add(items, "peach", 2, 0.3F);
        add(items, "pear", 2, 0.3F);
        add(items, "plum", 2, 0.3F);
        add(items, "almond", 2, 0.3F);
        add(items, "juniper_berries", () -> new Item(new Item.Properties()));
        add(items, "agave", () -> new Item(new Item.Properties()));
        add(items, "anise", () -> new Item(new Item.Properties()));
        add(items, "hops", () -> new Item(new Item.Properties()));
        add(items, "barley", () -> new Item(new Item.Properties()));
        add(items, "corn", 2, 0.3F);
        add(items, "rye", () -> new Item(new Item.Properties()));
        add(items, "soybean", 1, 0.6F);
        add(items, "rice", () -> new Item(new Item.Properties()));
        add(items, "spice_leaf", () -> new Item(new Item.Properties()));
        add(items, "lime", 1, 0.6F);
        add(items, "grapefruit", 2, 0.3F);
        add(items, "persimmon", 2, 0.3F);
        add(items, "starfruit", 2, 0.3F);
        add(items, "fig", 2, 0.3F);
        add(items, "mango", 2, 0.3F);
        add(items, "papaya", 2, 0.3F);
        addSlice(items, "tomato", 0, 0.15F);
        addSlice(items, "onion", 0, 0.3F);
        addSlice(items, "cucumber", 0, 0.3F);
        addSlice(items, "ananas", 1, 0.3F);
        addSlice(items, "lemon", 0, 0.15F);
        addSlice(items, "banana", 0, 0.15F);
        BLOCKS.register(bus);
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            CROPS.forEach(crop -> event.accept(crop.produce));
            SLICES.forEach(event::accept);
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            CROPS.forEach(crop -> event.accept(crop.seeds));
        }
    }

    private static void add(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        add(items, id, () -> new Item(food(nutrition, saturation)));
    }

    private static void add(DeferredRegister<Item> items, String id, Supplier<Item> produce) {
        CROPS.add(new Crop(items, id, produce));
    }

    private static void addSlice(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        SLICES.add(items.register(id + "_slice", () -> new Item(food(nutrition, saturation))));
    }

    private static Item.Properties food(int nutrition, float saturation) {
        return new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build());
    }

    private static final class Crop {
        private final RegistryObject<Item> produce;
        private final RegistryObject<Item> seeds;

        private Crop(DeferredRegister<Item> items, String id, Supplier<Item> produceItem) {
            produce = items.register(id, produceItem);
            var block = BLOCKS.register(id + "_crop", () -> new CropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)) {
                @Override
                protected ItemLike getBaseSeedId() {
                    return seedItem();
                }
            });
            seeds = items.register(id + "_seeds", () -> new ItemNameBlockItem(block.get(), new Item.Properties()));
        }

        private Item seedItem() {
            return seeds.get();
        }
    }
}

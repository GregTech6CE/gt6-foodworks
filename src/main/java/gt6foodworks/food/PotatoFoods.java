package gt6foodworks.food;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

final class PotatoFoods {
    private static final ResourceLocation PAPER_SCRAP = new ResourceLocation("gt6ce", "scrap_gt_paper");
    private static final ResourceLocation ALUMINIUM_SCRAP = new ResourceLocation("gt6ce", "scrap_gt_aluminium");
    private static final List<RegistryObject<Item>> FOODS = new ArrayList<>();

    private PotatoFoods() {}

    static void register(DeferredRegister<Item> items) {
        food(items, "potato_pieces", 1, 1.2F);
        food(items, "potato_strips", 1, 1.2F);
        food(items, "fries", 7, 1.2F);
        food(items, "fries_2", 7, 1.2F, () -> scrap(PAPER_SCRAP, 16));
        food(items, "potato_chips_raw", 1, 1.2F);
        food(items, "potato_chips", 7, 1.2F);
        food(items, "bag_of_potato_chips", 7, 1.2F, () -> scrap(ALUMINIUM_SCRAP, 2));
        food(items, "chili_chips", 7, 1.2F);
        food(items, "bag_of_chili_chips", 7, 1.2F, () -> scrap(ALUMINIUM_SCRAP, 2));
        food(items, "potato_on_a_stick", 1, 0.6F, () -> new ItemStack(Items.STICK));
        food(items, "roasted_potato_on_a_stick", 5, 1.2F, () -> new ItemStack(Items.STICK));
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        FOODS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        FOODS.add(items.register(id, () -> new Item(properties(nutrition, saturation))));
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation,
                             Supplier<ItemStack> remainder) {
        FOODS.add(items.register(id, () -> new PackagedFoodItem(properties(nutrition, saturation), remainder)));
    }

    private static Item.Properties properties(int nutrition, float saturation) {
        return new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build());
    }

    private static ItemStack scrap(ResourceLocation id, int count) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(id), count);
    }

    private static final class PackagedFoodItem extends Item {
        private final Supplier<ItemStack> remainder;

        private PackagedFoodItem(Properties properties, Supplier<ItemStack> remainder) {
            super(properties);
            this.remainder = remainder;
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack remaining = super.finishUsingItem(stack, level, entity);
            if (!level.isClientSide && entity instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack packaging = remainder.get();
                player.getInventory().add(packaging);
                if (!packaging.isEmpty()) {
                    player.drop(packaging, false);
                }
            }
            return remaining;
        }
    }
}

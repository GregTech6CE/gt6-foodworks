package gt6foodworks.food;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

final class HandheldFoods {
    private static final List<RegistryObject<Item>> CREATIVE_ITEMS = new ArrayList<>();

    private HandheldFoods() {}

    static void register(DeferredRegister<Item> items) {
        food(items, "veggie_burger", 4, 1.2F);
        food(items, "cheese_burger", 4, 1.4F);
        food(items, "hamburger", 6, 1.6F);
        CREATIVE_ITEMS.add(items.register("chum_burger", () -> new Item(chumProperties(6))));
        food(items, "soylent_burger", 5, 1.4F);
        food(items, "fish_burger", 6, 1.6F);
        food(items, "veggie_sandwich", 7, 1.2F, 32);
        food(items, "cheese_sandwich", 7, 1.4F, 32);
        food(items, "bacon_sandwich", 10, 1.8F, 32);
        food(items, "steak_sandwich", 10, 1.6F, 32);
        food(items, "large_veggie_sandwich", 15, 2.2F, 16);
        food(items, "large_cheese_sandwich", 15, 2.4F, 16);
        food(items, "large_bacon_sandwich", 20, 2.8F, 16);
        food(items, "large_steak_sandwich", 20, 2.6F, 16);

        food(items, "emerald_green_bar", 3, 1.4F);
        CREATIVE_ITEMS.add(items.register("chum", () -> new Item(chumProperties(5))));
        CREATIVE_ITEMS.add(items.register("chum_on_a_stick", ChumOnStickItem::new));
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_ITEMS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        food(items, id, nutrition, saturation, 64);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation, int stackSize) {
        CREATIVE_ITEMS.add(items.register(id, () -> new Item(new Item.Properties().stacksTo(stackSize).food(
                new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build()))));
    }

    private static Item.Properties chumProperties(int nutrition) {
        return new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition).saturationMod(1.6F).alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1000, 4), 1F)
                .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 0.8F)
                .build());
    }

    private static final class ChumOnStickItem extends Item {
        private ChumOnStickItem() {
            super(chumProperties(5).craftRemainder(Items.STICK));
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            var remaining = super.finishUsingItem(stack, level, entity);
            if (!(entity instanceof Player player) || player.getAbilities().instabuild) return remaining;
            var stick = new ItemStack(Items.STICK);
            if (remaining.isEmpty()) return stick;
            if (!level.isClientSide && !player.getInventory().add(stick)) player.drop(stick, false);
            return remaining;
        }
    }
}

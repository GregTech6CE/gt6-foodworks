package gregtechfood;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

final class EggFoods {
    private static final List<RegistryObject<Item>> ITEMS = new ArrayList<>();

    private EggFoods() {}

    static void register(DeferredRegister<Item> items) {
        ITEMS.add(items.register("egg", () -> new EggItem(new Item.Properties())));
        food(items, "boiled_egg", 2, 1.2F);
        food(items, "boiled_egg_2", 2, 1.2F);
        food(items, "fried_egg", 2, 1.2F);
        food(items, "scrambled_egg", 2, 1.2F);
        food(items, "sliced_egg", 1, 0.6F);
        food(items, "egg_yolk", 1, 1.2F);
        food(items, "egg_white", 1, 1.2F);
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        ITEMS.forEach(event::accept);
    }

    private static void food(DeferredRegister<Item> items, String id, int nutrition, float saturation) {
        ITEMS.add(items.register(id, () -> new EggItem(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build()))));
    }

    private static final class EggItem extends Item {
        private EggItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            String key = getDescriptionId() + ".tooltip";
            if (Language.getInstance().has(key)) {
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}

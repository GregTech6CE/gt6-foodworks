package gregtechfood;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

final class IceCreamFoods {
    private static final List<String> FLAVORS = List.of(
            "stracciatella", "raisin", "vanilla", "chocolate", "mocha", "caramel", "mint", "mint_chocolate_chip",
            "strawberry", "cherry", "blueberry", "currant", "blackberry", "raspberry", "cranberry",
            "gooseberry", "nutella", "lemon", "pistachio", "maple", "bacon", "kiwi", "melon", "banana", "grape",
            "apple", "peanut_butter", "ananas", "honey", "neapolitan", "spumoni_chocolate", "spumoni_vanilla", "superman");
    private static final List<RegistryObject<Item>> ITEMS = new ArrayList<>();

    static void register(DeferredRegister<Item> items) {
        var ordinary = new FoodProperties.Builder().nutrition(1).saturationMod(0.6F).build();
        var bear = new FoodProperties.Builder().nutrition(2).saturationMod(1.2F).build();
        var rainbow = new FoodProperties.Builder().nutrition(1).saturationMod(0.6F)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 10, 4), 1F)
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 200, 2), 1F).build();
        var chum = new FoodProperties.Builder().nutrition(1).saturationMod(0.6F).alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 250, 4), 1F)
                .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100, 1), 0.8F)
                .build();
        ITEMS.add(items.register("ice_cream", () -> new IceCreamItem(ordinary)));
        for (String flavor : FLAVORS) {
            ITEMS.add(items.register(flavor + "_ice_cream", () -> new IceCreamItem(ordinary)));
        }
        ITEMS.add(items.register("bear_ice_cream", () -> new IceCreamItem(bear)));
        ITEMS.add(items.register("chum_ice_cream", () -> new IceCreamItem(chum)));
        ITEMS.add(items.register("rainbow_ice_cream", () -> new IceCreamItem(rainbow)));
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        ITEMS.forEach(event::accept);
    }

    private static final class IceCreamItem extends Item {
        private IceCreamItem(FoodProperties food) {
            super(new Item.Properties().food(food));
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable(getDescriptionId() + ".tooltip"));
        }
    }

    private IceCreamFoods() {}
}

package gt6foodworks.food;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class CannedFoods {
    private static final List<String> SIZES = List.of("tiny", "small", "tall", "wide", "large", "huge");
    private static final List<RegistryObject<Item>> CREATIVE_ITEMS = new ArrayList<>();
    private static RegistryObject<Item> emptyCan;

    public enum Contents {
        UNKNOWN("unknown"), ROTTEN("rotten"), VEGETABLES("vegetables"), FRUITS("fruits"),
        BREAD("bread"), MEAT("meat"), FISH("fish"), CHUM("chum"), COOKIES("cookies"), AIR("air");

        private final String id;

        Contents(String id) {
            this.id = id;
        }

        private boolean spoiled() {
            return this == ROTTEN || this == CHUM;
        }

        private boolean catFood() {
            return this == MEAT || this == FISH;
        }
    }

    static void register(DeferredRegister<Item> items) {
        emptyCan = items.register("empty_food_can", () -> new Item(new Item.Properties()));
        CREATIVE_ITEMS.add(emptyCan);
        for (Contents contents : Contents.values()) {
            if (contents == Contents.AIR) continue;
            for (int size = 0; size < SIZES.size(); size++) {
                int serving = size + 1;
                String id = SIZES.get(size) + "_food_can_" + contents.id;
                RegistryObject<Item> item = items.register(id, () -> new CannedFoodItem(contents, serving));
                if (!contents.spoiled()) CREATIVE_ITEMS.add(item);
            }
        }
        for (String id : List.of("canned_air", "canned_hot_air", "canned_space_air")) {
            CREATIVE_ITEMS.add(items.register(id, () -> new CannedFoodItem(Contents.AIR, 0)));
        }
        registerNamed(items, Contents.FRUITS, 1, "melons", "blackberries", "blueberries", "raspberries",
                "cranberries", "gooseberries", "strawberries", "lemons", "pomegranates", "bananas",
                "grapes", "black_currants", "red_currants", "white_currants", "cherries", "kiwis", "coconuts");
        registerNamed(items, Contents.FRUITS, 2, "apples", "ananas", "green_apples", "yellow_apples", "dark_red_apples");
        registerNamed(items, Contents.VEGETABLES, 1, "beets", "carrots", "potatoes", "pumpkin",
                "tomatoes", "cucumbers", "pickles", "onions", "chili_peppers");
        registerNamed(items, Contents.UNKNOWN, 1, "hazelnuts", "pistachios");
        registerNamed(items, Contents.BREAD, 2, "sliced_baguette");
        registerNamed(items, Contents.BREAD, 4, "baguette");
        registerNamed(items, Contents.MEAT, 1,
                "raw_beef", "raw_chicken", "raw_mutton", "raw_pork", "raw_rabbit",
                "raw_ham", "ham_slices", "bacon", "raw_ribs", "raw_rib_eye_steak",
                "raw_dog", "raw_horse", "raw_mule", "raw_donkey");
        registerNamed(items, Contents.MEAT, 2, "rabbit");
        registerNamed(items, Contents.MEAT, 3, "chicken", "mutton");
        registerNamed(items, Contents.MEAT, 4, "beef", "pork", "dog", "horse", "donkey");
        registerNamed(items, Contents.MEAT, 5, "ham", "ribs", "rib_eye_steak", "mule");
        registerNamed(items, Contents.FISH, 1, "raw_cod", "raw_salmon", "tropical_fish", "pufferfish");
        registerNamed(items, Contents.FISH, 2, "cod");
        registerNamed(items, Contents.FISH, 3, "salmon");
        registerNamed(items, Contents.VEGETABLES, 1, "emerald_green");
        registerNamed(items, Contents.CHUM, 2, "chum");
        CREATIVE_ITEMS.add(items.register("cookie_tin", () -> new CannedFoodItem(Contents.COOKIES, 6)));
        CREATIVE_ITEMS.add(items.register("raisin_cookie_tin", () -> new CannedFoodItem(Contents.COOKIES, 6)));
        CREATIVE_ITEMS.add(items.register("chocolate_raisin_cookie_tin", () -> new CannedFoodItem(Contents.COOKIES, 6)));
        MinecraftForge.EVENT_BUS.addListener(CannedFoods::feedPet);
    }

    private static void registerNamed(DeferredRegister<Item> items, Contents contents, int serving, String... names) {
        for (String name : names) {
            CREATIVE_ITEMS.add(items.register("canned_" + name, () -> new CannedFoodItem(contents, serving)));
        }
    }

    static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_ITEMS.forEach(event::accept);
    }

    public static Contents contentsOf(ItemStack stack) {
        return stack.getItem() instanceof CannedFoodItem food ? food.contents : null;
    }

    private static void feedPet(PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof CannedFoodItem food
                && event.getTarget() instanceof LivingEntity target) {
            var result = food.interactLivingEntity(event.getItemStack(), event.getEntity(), target, event.getHand());
            if (result.consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }

    private static ItemStack emptyCanStack() {
        return new ItemStack(emptyCan.get());
    }

    private static void returnCan(Player player) {
        ItemStack can = emptyCanStack();
        if (!player.getInventory().add(can)) player.drop(can, false);
    }

    private static final class CannedFoodItem extends Item {
        private final Contents contents;

        private CannedFoodItem(Contents contents, int serving) {
            super(new Item.Properties().food(foodProperties(contents, serving)));
            this.contents = contents;
        }

        private static FoodProperties foodProperties(Contents contents, int serving) {
            var builder = new FoodProperties.Builder().nutrition(2 * serving).saturationMod(serving / 10F);
            switch (contents) {
                case AIR -> builder.alwaysEat();
                case ROTTEN -> builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300), (3 + serving) / 10F);
                case CHUM -> builder
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1000, 4), 1F)
                        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 0.8F);
                default -> { }
            }
            return builder.build();
        }

        @Override
        public boolean hasCraftingRemainingItem(ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack getCraftingRemainingItem(ItemStack stack) {
            return emptyCanStack();
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack) {
            return contents == Contents.AIR ? UseAnim.DRINK : UseAnim.EAT;
        }

        @Override
        public int getUseDuration(ItemStack stack) {
            return Math.max(16, getFoodProperties().getNutrition() * 8);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            if (!(entity instanceof Player player)) return stack;
            ItemStack remaining = super.finishUsingItem(stack, level, player);
            if (!level.isClientSide && contents == Contents.AIR) {
                player.setAirSupply(player.getAirSupply() + 400);
            }
            if (player.getAbilities().instabuild) return remaining;
            if (remaining.isEmpty()) return emptyCanStack();
            if (!level.isClientSide) returnCan(player);
            return remaining;
        }

        @Override
        public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                       InteractionHand hand) {
            boolean fed = false;
            if (contents == Contents.MEAT && target instanceof Wolf wolf && wolf.isTame()) {
                if (wolf.getHealth() < 20) {
                    if (!player.level().isClientSide) wolf.heal(getFoodProperties().getNutrition());
                    fed = true;
                } else if (wolf.getAge() == 0 && !wolf.isInLove()) {
                    if (!player.level().isClientSide) wolf.setInLove(player);
                    fed = true;
                }
            } else if (contents.catFood() && target instanceof Cat cat && cat.isTame()
                    && cat.getAge() == 0 && !cat.isInLove()) {
                if (!player.level().isClientSide) cat.setInLove(player);
                fed = true;
            }
            if (!fed) return InteractionResult.PASS;
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                if (!player.level().isClientSide) returnCan(player);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            if (contents == Contents.AIR) {
                tooltip.add(Component.translatable("tooltip.gt6food.canned_air"));
                return;
            }
            var food = getFoodProperties();
            tooltip.add(Component.translatable("tooltip.gt6food.canned_food.nutrition", food.getNutrition(),
                    food.getSaturationModifier()).withStyle(ChatFormatting.RED));
            if (contents.spoiled()) {
                tooltip.add(Component.translatable("tooltip.gt6food.canned_food.rotten").withStyle(ChatFormatting.DARK_RED));
            }
            if (contents.catFood()) tooltip.add(Component.translatable("tooltip.gt6food.canned_food.cat"));
            if (contents == Contents.MEAT) tooltip.add(Component.translatable("tooltip.gt6food.canned_food.dog"));
        }
    }

    private CannedFoods() {}
}

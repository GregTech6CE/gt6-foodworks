package gregtechfood.drink;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class SpecialConsumables {
    private static final List<RegistryObject<Item>> PILLS = new ArrayList<>();

    private SpecialConsumables() {}

    static void register(DeferredRegister<Item> items) {
        DrinkRegistry.drink(items, "rotten_drink", 0xFF694636, 0, 0.0F,
                new DrinkRegistry.DrinkEffect(MobEffects.HUNGER, 100, 1, 1.0F));
        DrinkRegistry.registerBottle(items, "holy_water", 0xFFA9AAFF, HolyWaterItem::new);
        DrinkRegistry.drink(items, "bawls", 0xFF67B931, 2, 0.5F,
                new DrinkRegistry.DrinkEffect(MobEffects.JUMP, 1200, 1, 0.9F),
                new DrinkRegistry.DrinkEffect(MobEffects.MOVEMENT_SLOWDOWN, 300, 0, 0.9F));
        DrinkRegistry.drink(items, "medicine", 0xFFFFA8C6, 0, 0.0F,
                new DrinkRegistry.DrinkEffect(MobEffects.REGENERATION, 120, 4, 1.0F));
        DrinkRegistry.drink(items, "laxative", 0xFF8D4A71, 0, 0.0F,
                new DrinkRegistry.DrinkEffect(MobEffects.HUNGER, 300, 10, 1.0F));

        pill(items, "peppermint", MobEffects.MOVEMENT_SLOWDOWN);
        pill(items, "blue_pill", MobEffects.CONFUSION);
        pill(items, "red_pill", MobEffects.BLINDNESS);
        pill(items, "antidote", MobEffects.POISON);
    }

    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        PILLS.forEach(event::accept);
    }

    private static void pill(DeferredRegister<Item> items, String name, MobEffect effect) {
        PILLS.add(items.register(name, () -> new CurePillItem(effect)));
    }

    public static final class CurePillItem extends Item {
        private final MobEffect curedEffect;

        private CurePillItem(MobEffect curedEffect) {
            super(new Properties().food(new FoodProperties.Builder().alwaysEat().build()));
            this.curedEffect = curedEffect;
        }

        public void cure(LivingEntity entity) {
            entity.removeEffect(curedEffect);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack remaining = super.finishUsingItem(stack, level, entity);
            if (!level.isClientSide) {
                cure(entity);
            }
            return remaining;
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.gt6food.pill.cures", curedEffect.getDisplayName()));
        }
    }

    private static final class HolyWaterItem extends BottledFoodItem {
        private HolyWaterItem() {
            super(new FoodProperties.Builder().alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 600, 1), 1.0F)
                            .build(),
                    UseAnim.DRINK, true, false, false, true);
        }

        @Override
        public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
            if (!(target instanceof ZombieVillager zombie) || !zombie.isAlive() || zombie.isConverting()
                    || !zombie.hasEffect(MobEffects.WEAKNESS)) {
                return InteractionResult.PASS;
            }
            if (!zombie.level().isClientSide) {
                ZombieConversion.start(zombie, player.getUUID(), 500 + zombie.getRandom().nextInt(1000));
                if (!player.getAbilities().instabuild) {
                    // Vanilla clears the hand after interaction if the original stack was emptied.
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack.copy(), player,
                            new ItemStack(Items.GLASS_BOTTLE)));
                }
            }
            return InteractionResult.sidedSuccess(zombie.level().isClientSide);
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.gt6food.holy_water.cures_zombie"));
        }
    }

    private static final class ZombieConversion {
        // Use the vanilla conversion path so villager data and Forge conversion events are preserved.
        private static final Method START_CONVERTING = ObfuscationReflectionHelper.findMethod(
                ZombieVillager.class, FMLEnvironment.production ? "m_34383_" : "startConverting", UUID.class, int.class);

        private static void start(ZombieVillager zombie, UUID player, int ticks) {
            try {
                START_CONVERTING.invoke(zombie, player, ticks);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to start holy water's zombie villager conversion", exception);
            }
        }
    }
}

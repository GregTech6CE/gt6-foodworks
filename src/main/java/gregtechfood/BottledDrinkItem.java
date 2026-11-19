package gregtechfood;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

class BottledDrinkItem extends Item {
    private final UseAnim useAnimation;
    private final boolean curesEffects;
    private final boolean extinguishes;
    private final boolean explosive;
    private final boolean glint;

    BottledDrinkItem(FoodProperties food) {
        this(food, UseAnim.DRINK, false);
    }

    BottledDrinkItem(FoodProperties food, UseAnim useAnimation, boolean curesEffects) {
        this(food, useAnimation, curesEffects, false, false, false);
    }

    BottledDrinkItem(FoodProperties food, UseAnim useAnimation, boolean curesEffects,
                     boolean extinguishes, boolean explosive, boolean glint) {
        super(new Item.Properties().stacksTo(16).food(food));
        this.useAnimation = useAnimation;
        this.curesEffects = curesEffects;
        this.extinguishes = extinguishes;
        this.explosive = explosive;
        this.glint = glint;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return useAnimation;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 48;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return glint || super.isFoil(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (useAnimation == UseAnim.DRINK) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    void applyDrinkEffects(LivingEntity entity) {
        applyBeforeConsumption(entity);
        for (var entry : getFoodProperties().getEffects()) {
            var effect = entry.getFirst();
            if (effect == null || entity.getRandom().nextFloat() >= entry.getSecond()) {
                continue;
            }
            var type = effect.getEffect();
            if (type.isInstantenous()) {
                type.applyInstantenousEffect(entity, entity, entity, effect.getAmplifier(), 1.0);
            } else {
                entity.addEffect(new MobEffectInstance(effect));
            }
        }
        applyAfterConsumption(entity);
    }

    private void applyBeforeConsumption(LivingEntity entity) {
        if (curesEffects) {
            entity.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }
        if (extinguishes) {
            entity.clearFire();
        }
    }

    private void applyAfterConsumption(LivingEntity entity) {
        if (explosive) {
            Level level = entity.level();
            level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), 4, true, Level.ExplosionInteraction.TNT);
            entity.hurt(entity.damageSources().explosion(entity, entity), Float.MAX_VALUE);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            applyBeforeConsumption(entity);
        }
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            applyAfterConsumption(entity);
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return remaining;
        }

        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if (remaining.isEmpty()) {
            return bottle;
        }
        if (!level.isClientSide && entity instanceof Player player && !player.getInventory().add(bottle)) {
            player.drop(bottle, false);
        }
        return remaining;
    }
}

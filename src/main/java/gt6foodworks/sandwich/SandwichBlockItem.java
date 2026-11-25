package gt6foodworks.sandwich;

import gt6foodworks.drink.BottledFoodItem;
import gt6foodworks.drink.SpecialConsumables.CurePillItem;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

final class SandwichBlockItem extends BlockItem {
    SandwichBlockItem(Block block) {
        super(block, new Properties().stacksTo(SandwichBlockEntity.HEIGHT));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        return player == null || player.isShiftKeyDown() ? super.useOn(context) : InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.canEat(false)) return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Math.max(32, nutrition(SandwichBlockEntity.itemLayers(stack), null).hunger() * 8);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            var layers = SandwichBlockEntity.itemLayers(stack);
            var food = nutrition(layers, player);
            player.getFoodData().eat(food.hunger(), food.saturation());
            for (ItemStack ingredient : layers) applyIngredientEffects(ingredient, player);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, 1F);
            player.gameEvent(GameEvent.EAT);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flags) {
        var layers = SandwichBlockEntity.itemLayers(stack);
        var food = nutrition(layers, null);
        tooltip.add(Component.translatable("tooltip.gt6food.sandwich.food", food.hunger(), food.saturation())
                .withStyle(ChatFormatting.RED));
        for (int i = layers.length - 1; i >= 0; i--) {
            if (!layers[i].isEmpty()) tooltip.add(layers[i].getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.gt6food.sandwich.use").withStyle(ChatFormatting.DARK_GRAY));
    }

    private record Nutrition(int hunger, float saturation) {}

    private static Nutrition nutrition(ItemStack[] layers, LivingEntity entity) {
        int hunger = 0;
        float saturation = 0;
        for (ItemStack ingredient : layers) {
            if (ingredient.isEmpty()) continue;
            var food = ingredient.getFoodProperties(entity);
            hunger += food == null ? 1 : Math.max(1, food.getNutrition());
            if (food != null) saturation = Math.max(saturation, food.getSaturationModifier());
        }
        return new Nutrition(hunger, saturation + 0.5F);
    }

    private static void applyIngredientEffects(ItemStack ingredient, Player player) {
        if (ingredient.isEmpty()) return;
        if (ingredient.getItem() instanceof BottledFoodItem drink) {
            drink.applyDrinkEffects(player);
            return;
        }
        if (ingredient.getItem() instanceof CurePillItem pill) {
            pill.cure(player);
            return;
        }
        if (ingredient.is(Items.HONEY_BOTTLE)) player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        var food = ingredient.getFoodProperties(player);
        if (food == null) return;
        for (var effect : food.getEffects()) {
            if (player.getRandom().nextFloat() < effect.getSecond()) {
                player.addEffect(new MobEffectInstance(effect.getFirst()));
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return SandwichRenderer.itemRenderer();
            }
        });
    }
}

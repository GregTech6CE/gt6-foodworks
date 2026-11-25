package gt6foodworks.drink;

import gt6foodworks.GT6Foodworks;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = GT6Foodworks.MOD_ID)
public final class VesselDrinking {
    private static final int PORTION = 250;
    private static final Set<String> VESSELS = Set.of("fluid_cup", "fluid_jug", "fluid_thermos");

    private VesselDrinking() {}

    @SubscribeEvent
    public static void start(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.getCount() != 1 || !isVessel(stack) || event.getEntity().isShiftKeyDown()) {
            return;
        }
        if (bottle(FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY)) != null) {
            event.getEntity().startUsingItem(event.getHand());
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void finish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack before = event.getItem();
        ItemStack result = event.getResultStack();
        if (before.getCount() != 1 || !isVessel(before) || !ItemStack.matches(before, result)) {
            return;
        }
        FluidUtil.getFluidHandler(result).ifPresent(handler -> {
            if (drink(player, handler)) {
                event.setResultStack(handler.getContainer());
            }
        });
    }

    @SubscribeEvent
    public static void drinkFromBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (event.getUseBlock() == Event.Result.DENY || player.isShiftKeyDown()
                || FluidUtil.getFluidHandler(event.getItemStack()).isPresent()) {
            return;
        }
        var level = event.getLevel();
        var pos = event.getPos();
        var stack = level.getBlockState(pos).getBlock().asItem().getDefaultInstance();
        if (!isVessel(stack) || !level.mayInteract(player, pos)) {
            return;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id.getPath().equals("fluid_jug") && event.getFace() != Direction.UP) {
            return;
        }
        var entity = level.getBlockEntity(pos);
        if (entity == null) {
            return;
        }
        entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            if (level.isClientSide ? bottle(handler.drain(PORTION, FluidAction.SIMULATE)) != null : drink(player, handler)) {
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        });
    }

    private static boolean isVessel(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getNamespace().equals("gt6ce") && VESSELS.contains(id.getPath());
    }

    private static BottledFoodItem bottle(FluidStack fluid) {
        if (fluid.getAmount() < PORTION) {
            return null;
        }
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        if (id != null && id.getNamespace().equals(GT6Foodworks.MOD_ID)
                && ForgeRegistries.ITEMS.getValue(id) instanceof BottledFoodItem item) {
            return item;
        }
        return null;
    }

    private static boolean drink(Player player, IFluidHandler handler) {
        FluidStack serving = handler.drain(PORTION, FluidAction.SIMULATE);
        BottledFoodItem item = bottle(serving);
        if (item == null || handler.drain(serving, FluidAction.EXECUTE).getAmount() != PORTION) {
            return false;
        }
        var food = item.getFoodProperties();
        player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
        item.applyDrinkEffects(player);
        var sound = item.getUseAnimation(item.getDefaultInstance()) == net.minecraft.world.item.UseAnim.EAT
                ? item.getEatingSound() : item.getDrinkingSound();
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 0.5F, 1.0F);
        return true;
    }
}

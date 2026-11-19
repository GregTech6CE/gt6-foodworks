package gregtechfood;

import gregtech.content.data.MachineRecipeMaps;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("gt6food")
@PrefixGameTestTemplate(false)
public final class AppleJuiceGameTests {
    @GameTest(template = "test_empty")
    public static void craftPressAndBottle(GameTestHelper helper) {
        var level = helper.getLevel();
        var pieces = addonItem("apple_pieces");
        var juice = Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gt6food", "apple_juice")));
        var oldJuice = Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gt6ce", "binnie_juiceapple")));
        var original = MachineRecipeMaps.Juicer.findRecipe(List.of(new ItemStack(Items.APPLE)), List.of(), true, 1, 3);
        helper.assertTrue(original != null && original.fluidOutputs.length == 1 && original.fluidOutputs[0].getFluid() == oldJuice,
                "Whole apples must still produce the original GT6CE juice");

        var grid = craftingGrid();
        grid.setItem(0, new ItemStack(Items.APPLE));
        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level).orElseThrow();
        var crafted = recipe.assemble(grid, level.registryAccess());
        helper.assertTrue(crafted.is(pieces), "A whole apple crafts into addon apple pieces");

        var input = List.of(crafted);
        var pressed = MachineRecipeMaps.Juicer.findRecipe(input, List.of(), true, 1, 3);
        helper.assertTrue(pressed != null && pressed.fluidOutputs[0].isFluidEqual(new FluidStack(juice, 75))
                        && pressed.fluidOutputs[0].getAmount() == 75
                        && pressed.outputChances[0] == 7000,
                "The addon juicer recipe must provide 75 mB of addon juice and the original residue chance");

        var juicer = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("gt6ce", "juicer")));
        var position = new BlockPos(2, 1, 2);
        helper.setBlock(position, juicer);
        var blockEntity = Objects.requireNonNull(level.getBlockEntity(helper.absolutePos(position)));
        var tank = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .orElseThrow(() -> new IllegalStateException("The manual juicer must expose a fluid output"));
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(pieces, 4));
        for (int i = 0; i < 4; i++) helper.useBlock(position, player);
        helper.assertTrue(player.getMainHandItem().isEmpty(), "The manual juicer must consume four pieces");
        helper.assertTrue(tank.getFluidInTank(0).getFluid() == juice && tank.getFluidInTank(0).getAmount() == 300,
                "Four interactions must collect 300 mB of the addon juice in the juicer");

        FluidStack portion = tank.drain(250, IFluidHandler.FluidAction.EXECUTE);
        var filling = MachineRecipeMaps.Canner.findRecipe(
                List.of(new ItemStack(Items.GLASS_BOTTLE)), List.of(portion), true, 2, 2);
        helper.assertTrue(filling != null && filling.itemOutputs[0].is(addonItem("apple_juice"))
                        && filling.fluidInputs[0].getAmount() == 250,
                "The canner must match a glass bottle and 250 mB to make one bottle of addon juice");

        var emptying = MachineRecipeMaps.Canner.findRecipe(
                List.of(filling.itemOutputs[0]), List.of(), true, 2, 2);
        helper.assertTrue(emptying != null && emptying.itemOutputs[0].is(Items.GLASS_BOTTLE)
                        && emptying.fluidOutputs[0].getFluid() == juice
                        && emptying.fluidOutputs[0].getAmount() == 250,
                "The canner must recover both the same juice and the empty bottle");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void drinkingReturnsBottlesInSurvival(GameTestHelper helper) {
        var juice = addonItem("apple_juice");
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getFoodData().setFoodLevel(4);
        var single = new ItemStack(juice);
        player.setItemInHand(InteractionHand.MAIN_HAND, single);
        helper.assertTrue(juice.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME,
                "A hungry player must be able to drink");
        var result = juice.finishUsingItem(single, helper.getLevel(), player);
        player.stopUsingItem();
        helper.assertTrue(result.is(Items.GLASS_BOTTLE) && result.getCount() == 1
                        && player.getFoodData().getFoodLevel() == 10,
                "Drinking the last juice returns one glass bottle and restores six hunger points");

        var stack = new ItemStack(juice, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        result = juice.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(result.is(juice) && result.getCount() == 1
                        && player.getInventory().countItem(Items.GLASS_BOTTLE) == 1,
                "Drinking from a stack consumes one juice and puts one glass bottle in inventory");

        player.getAbilities().instabuild = true;
        result = juice.finishUsingItem(stack, helper.getLevel(), player);
        helper.assertTrue(result.is(juice) && result.getCount() == 1
                        && player.getInventory().countItem(Items.GLASS_BOTTLE) == 1,
                "Creative drinking keeps the juice and leaves inventory as it was");
        helper.succeed();
    }

    private static Item addonItem(String path) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("gt6food", path)), path);
    }

    private static TransientCraftingContainer craftingGrid() {
        var menu = new AbstractContainerMenu(MenuType.CRAFTING, 0) {
            @Override
            public ItemStack quickMoveStack(Player player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        };
        return new TransientCraftingContainer(menu, 2, 2);
    }

    private AppleJuiceGameTests() {}
}

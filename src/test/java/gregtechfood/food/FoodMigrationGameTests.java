package gregtechfood.food;

import gregtech.content.data.MachineRecipeMaps;
import gregtech.engine.recipe.Recipe;
import gregtech.engine.recipe.RecipeMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("gt6food")
@PrefixGameTestTemplate(false)
public final class FoodMigrationGameTests {
    @GameTest(template = "test_empty")
    public static void pressAndBottleCarrotAndMelon(GameTestHelper helper) {
        checkJuiceChain(helper, "carrot", 75, "gt6food:vegetable_remains", 7000);
        checkJuiceChain(helper, "melon", 200, "gt6food:fruit_remains", 6000);
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void mixAndFermentAddonAppleJuice(GameTestHelper helper) {
        var mixed = recipe(MachineRecipeMaps.Mixer, List.of(),
                List.of(fluid("gt6ce:ice", 125), fluid("gt6food:apple_juice", 125)), "apple smoothie");
        assertFluidOutput(helper, mixed, fluid("gt6food:apple_smoothie", 250));

        var circuit = stack("gt6ce:integrated_circuit_0");
        var cider = recipe(MachineRecipeMaps.Fermenter, List.of(circuit),
                List.of(fluid("gt6food:apple_juice", 50)), "apple cider");
        assertFluidOutput(helper, cider, fluid("gt6food:cider", 25));
        helper.assertTrue(cider.isCatalystInput(0), "Cider fermentation retains the circuit");

        var vinegar = recipe(MachineRecipeMaps.Fermenter, List.of(circuit),
                List.of(fluid("gt6food:cider", 50)), "apple cider vinegar");
        assertFluidOutput(helper, vinegar, fluid("gt6food:apple_cider_vinegar", 10));
        helper.assertTrue(vinegar.isCatalystInput(0), "Vinegar fermentation retains the circuit");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void sliceFryAndPackagePotatoes(GameTestHelper helper) {
        var grid = stack("gt6ce:slicer_shape_grid");
        var core = recipe(MachineRecipeMaps.Slicer, List.of(new ItemStack(Items.POTATO), grid), List.of(), "whole potato");
        helper.assertTrue(core.itemOutputs[0].is(item("gt6ce:potato_strips")), "Whole potato still makes core potato strips");

        var sliced = recipe(MachineRecipeMaps.Slicer, List.of(stack("gt6food:potato_pieces"), grid), List.of(), "addon potato strips");
        helper.assertTrue(sliced.itemOutputs[0].is(item("gt6food:potato_strips")) && sliced.isCatalystInput(1),
                "Addon potato pieces yield strips and retain the slicing blade");

        var fried = recipe(MachineRecipeMaps.Bath, List.of(sliced.itemOutputs[0]),
                List.of(fluid("gt6ce:hotfryingoil", 2)), "addon fries");
        helper.assertTrue(fried.itemOutputs[0].is(item("gt6food:fries")), "The bath fries sliced addon potatoes");

        var boxed = recipe(MachineRecipeMaps.Boxinator, List.of(fried.itemOutputs[0], stack("gt6ce:plate_double_paper")),
                List.of(), "packaged addon fries");
        helper.assertTrue(boxed.itemOutputs[0].is(item("gt6food:fries_2")), "The boxinator packages addon fries");

        var unboxed = recipe(MachineRecipeMaps.Unboxinator, List.of(boxed.itemOutputs[0]), List.of(), "unboxed addon fries");
        helper.assertTrue(unboxed.itemOutputs.length == 2
                        && unboxed.itemOutputs[0].is(item("gt6food:fries"))
                        && unboxed.itemOutputs[1].is(item("gt6ce:scrap_gt_paper"))
                        && unboxed.itemOutputs[1].getCount() == 16,
                "Unboxing returns addon fries and 16 paper scraps");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void fillAndOpenAddonCans(GameTestHelper helper) {
        var emptyCan = item("gt6food:empty_food_can");
        var packed = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(emptyCan)),
                List.of(), "rotten canned food");
        var rottenCan = packed.itemOutputs[0];
        helper.assertTrue(rottenCan.is(item("gt6food:small_food_can_rotten"))
                        && rottenCan.getCraftingRemainingItem().is(emptyCan),
                "The addon canner fills the rotten ration, which leaves its can in crafting");

        var air = fluid("gt6ce:air", 16000);
        var filled = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(emptyCan)), List.of(air), "canned air");
        var cannedAir = item("gt6food:canned_air");
        helper.assertTrue(filled.itemOutputs[0].is(cannedAir), "The addon canner fills an air can");
        var drained = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(cannedAir)), List.of(), "drained air can");
        assertFluidOutput(helper, drained, air);
        helper.assertTrue(drained.itemOutputs[0].is(emptyCan), "Draining canned air returns an empty can");

        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getFoodData().setFoodLevel(20);
        player.setAirSupply(0);
        var serving = new ItemStack(cannedAir);
        player.setItemInHand(InteractionHand.MAIN_HAND, serving);
        helper.assertTrue(cannedAir.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME,
                "The player can open canned air with a full hunger bar");
        var remainder = cannedAir.finishUsingItem(serving, helper.getLevel(), player);
        player.stopUsingItem();
        helper.assertTrue(remainder.is(emptyCan) && player.getAirSupply() == 400,
                "Opening a single air can supplies 400 air ticks and returns an empty can");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void pressAndBakeBread(GameTestHelper helper) {
        var dough = new ItemStack(item("gt6food:dough"), 2);
        var pressed = recipe(MachineRecipeMaps.Press, List.of(dough, stack("gt6ce:foodmold_shape_bread")),
                List.of(), "bread dough");
        helper.assertTrue(pressed.itemOutputs[0].is(item("gt6food:dough_bread")) && pressed.isCatalystInput(1),
                "The press shapes two dough and retains the bread mold");

        var smoker = new SimpleContainer(pressed.itemOutputs[0]);
        var level = helper.getLevel();
        var baking = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, smoker, level).orElseThrow();
        helper.assertTrue(baking.assemble(smoker, level.registryAccess()).is(Items.BREAD),
                "The smoker bakes shaped addon dough into bread");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void flavorAddonIceCream(GameTestHelper helper) {
        var flavored = recipe(MachineRecipeMaps.Mixer, List.of(stack("gt6food:ice_cream")),
                List.of(fluid("gt6food:apple_juice", 50)), "apple ice cream");
        helper.assertTrue(flavored.itemOutputs[0].is(item("gt6food:apple_ice_cream")),
                "Addon apple juice flavors addon ice cream");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void roastHamWithAndWithoutOil(GameTestHelper helper) {
        var ham = stack("gt6food:raw_ham");
        var plain = recipe(MachineRecipeMaps.Oven, List.of(ham), List.of(), "plain ham");
        var oiled = recipe(MachineRecipeMaps.Oven, List.of(ham), List.of(fluid("gt6ce:hotfryingoil", 50)), "oiled ham");
        var cooked = item("gt6food:cooked_ham");
        helper.assertTrue(plain.itemOutputs[0].is(cooked) && plain.itemOutputs[0].getCount() == 1,
                "The oven cooks a raw ham into one cooked ham without oil");
        helper.assertTrue(oiled.itemOutputs[0].is(cooked) && oiled.itemOutputs[0].getCount() == 2,
                "The oven cooks a raw ham into two cooked hams with frying oil");
        helper.succeed();
    }

    private static void checkJuiceChain(GameTestHelper helper, String produce, int pressedAmount, String residue, long chance) {
        String id = "gt6food:" + produce + "_juice";
        var pressed = recipe(MachineRecipeMaps.Juicer, List.of(stack("gt6food:" + produce + "_pieces")), List.of(), id);
        assertFluidOutput(helper, pressed, fluid(id, pressedAmount));
        helper.assertTrue(pressed.itemOutputs.length == 1 && pressed.itemOutputs[0].is(item(residue))
                        && pressed.outputChances[0] == chance,
                "Juicing " + produce + " gives the expected residue and chance");

        var portion = fluid(id, 250);
        var bottled = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(Items.GLASS_BOTTLE)), List.of(portion), id);
        helper.assertTrue(bottled.itemOutputs[0].is(item(id)) && bottled.fluidInputs[0].getAmount() == 250,
                "Bottling " + produce + " requires 250 mB of addon juice");

        var drained = recipe(MachineRecipeMaps.Canner, List.of(bottled.itemOutputs[0]), List.of(), "drained " + id);
        assertFluidOutput(helper, drained, portion);
        helper.assertTrue(drained.itemOutputs[0].is(Items.GLASS_BOTTLE), "Draining " + produce + " returns the bottle");
    }

    private static void assertFluidOutput(GameTestHelper helper, Recipe recipe, FluidStack expected) {
        helper.assertTrue(recipe.fluidOutputs.length == 1 && recipe.fluidOutputs[0].isFluidEqual(expected)
                        && recipe.fluidOutputs[0].getAmount() == expected.getAmount(),
                "Expected " + expected.getAmount() + " mB of " + ForgeRegistries.FLUIDS.getKey(expected.getFluid()));
    }

    private static Recipe recipe(RecipeMap map, List<ItemStack> items, List<FluidStack> fluids, String name) {
        return Objects.requireNonNull(map.findRecipe(items, fluids, true, items.size(), 4), "Missing recipe for " + name);
    }

    private static ItemStack stack(String id) {
        return new ItemStack(item(id));
    }

    private static Item item(String id) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id);
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(id)), id), amount);
    }

    private FoodMigrationGameTests() {}
}

package gregtechfood.food;

import gregtech.content.data.ImportedMaterialData;
import gregtech.content.data.MachineRecipeMaps;
import gregtech.content.data.MaterialPrefix;
import gregtech.content.registry.GTItems;
import gregtech.engine.recipe.Recipe;
import gregtech.engine.recipe.RecipeMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("gt6food")
@PrefixGameTestTemplate(false)
public final class BreakfastGameTests {
    @GameTest(template = "test_empty")
    public static void processMilkIntoDairy(GameTestHelper helper) {
        var emptied = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(Items.MILK_BUCKET)), List.of(), "milk bucket");
        assertItemOutput(helper, emptied, Items.BUCKET, 1);
        assertFluidOutput(helper, emptied, "gt6food:milk", 1000);

        var separated = recipe(MachineRecipeMaps.Centrifuge, List.of(), List.of(fluid("gt6food:milk", 50)), "heavy cream");
        assertFluidOutput(helper, separated, "gt6food:heavy_cream", 50);

        var butter = recipe(MachineRecipeMaps.Centrifuge, List.of(), List.of(fluid("gt6food:heavy_cream", 250)), "butter");
        assertItemOutput(helper, butter, item("gt6food:butter"), 1);
        var salt = GTItems.getStack(MaterialPrefix.dustTiny, ImportedMaterialData.NaCl);
        var salted = recipe(MachineRecipeMaps.Mixer, List.of(butter.itemOutputs[0], salt), List.of(), "salted butter");
        assertItemOutput(helper, salted, item("gt6food:salted_butter"), 1);
        var smallSalt = GTItems.getStack(MaterialPrefix.dustSmall, ImportedMaterialData.NaCl);
        var frozen = recipe(MachineRecipeMaps.CryoMixer, List.of(smallSalt),
                List.of(fluid("minecraft:water", 250), fluid("gt6food:heavy_cream", 250)), "ice cream from addon cream");
        assertItemOutput(helper, frozen, item("gt6food:ice_cream"), 1);

        var cheese = recipe(MachineRecipeMaps.Coagulator, List.of(), List.of(emptied.fluidOutputs[0]), "cheese");
        assertItemOutput(helper, cheese, item("gt6food:cheese"), 1);
        var sliced = recipe(MachineRecipeMaps.Slicer,
                List.of(cheese.itemOutputs[0], stack("gt6ce:slicer_shape_flat")), List.of(), "cheese slices");
        assertItemOutput(helper, sliced, item("gt6food:cheese_slice"), 4);
        helper.assertTrue(sliced.isCatalystInput(1), "The Slicer retains the flat blade");

        var core = recipe(MachineRecipeMaps.Centrifuge, List.of(), List.of(fluid("gt6ce:milk", 50)), "core dairy");
        assertFluidOutput(helper, core, "gt6ce:grcmilk_cream", 50);
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void bottleAndDrinkMilkWhenFull(GameTestHelper helper) {
        var milk = item("gt6food:milk");
        assertBottleRoundTrip(helper, "gt6food:milk");

        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getAbilities().invulnerable = false;
        player.getFoodData().setFoodLevel(20);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        helper.assertTrue(player.hasEffect(MobEffects.POISON), "The player starts with poison");
        var serving = new ItemStack(milk);
        player.setItemInHand(InteractionHand.MAIN_HAND, serving);
        helper.assertTrue(milk.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult()
                        == InteractionResult.CONSUME,
                "Milk can be drunk with full hunger");
        var remainder = milk.finishUsingItem(serving, helper.getLevel(), player);
        player.stopUsingItem();
        helper.assertTrue(remainder.is(Items.GLASS_BOTTLE) && !player.hasEffect(MobEffects.POISON),
                "Drinking milk removes poison and returns a glass bottle");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void makeSpoiledMilkAlongsideCoreRecipe(GameTestHelper helper) {
        var milkDust = GTItems.getStack(MaterialPrefix.dust, ImportedMaterialData.Milk);
        var soda = fluid("gt6ce:soda", 500);
        var circuit = stack("gt6ce:integrated_circuit_0");

        var core = recipe(MachineRecipeMaps.Mixer, List.of(milkDust), List.of(soda), "core spoiled milk");
        assertFluidOutput(helper, core, "gt6ce:spoiledmilk", 1000);
        var mixed = recipe(MachineRecipeMaps.Mixer, List.of(milkDust, circuit), List.of(soda), "addon spoiled milk");
        assertFluidOutput(helper, mixed, "gt6food:milk_2", 1000);
        helper.assertTrue(retains(mixed, circuit.getItem()), "Making addon spoiled milk in the Mixer retains the circuit");

        var fermented = recipe(MachineRecipeMaps.Fermenter, List.of(circuit), List.of(fluid("gt6food:milk", 50)),
                "fermented addon milk");
        assertFluidOutput(helper, fermented, "gt6food:milk_2", 50);
        helper.assertTrue(retains(fermented, circuit.getItem()), "Fermenting addon milk retains the circuit");
        assertBottleRoundTrip(helper, "gt6food:milk_2");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void mixAndBottleNewSmoothies(GameTestHelper helper) {
        var ice = fluid("gt6ce:ice", 125);
        for (String fruit : List.of("ananas", "banana", "gooseberry", "lemon", "pomegranate", "strawberry")) {
            String juice = "gt6food:" + fruit + "_juice";
            String smoothie = "gt6food:" + fruit + "_smoothie";
            var mixed = recipe(MachineRecipeMaps.Mixer, List.of(), List.of(ice, fluid(juice, 125)), smoothie);
            assertFluidOutput(helper, mixed, smoothie, 250);
            helper.assertTrue(mixed.fluidInputs.length == 2 && mixed.fluidInputs[0].getAmount() == 125
                            && mixed.fluidInputs[1].getAmount() == 125,
                    "The Mixer uses 125 mB of each input for " + smoothie);
            assertBottleRoundTrip(helper, smoothie);
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void separateScrambleAndSliceEggs(GameTestHelper helper) {
        var raw = stack("gt6food:egg");
        var separated = recipe(MachineRecipeMaps.Juicer, List.of(raw), List.of(), "egg separation");
        var white = item("gt6food:egg_white");
        var yolk = item("gt6food:egg_yolk");
        helper.assertTrue(contains(separated, white) && contains(separated, yolk) && contains(separated, Items.BONE_MEAL),
                "The Juicer separates egg white and yolk with bonemeal as its residue");
        var scrambled = recipe(MachineRecipeMaps.Mixer, List.of(new ItemStack(white), new ItemStack(yolk)),
                List.of(), "scrambled egg");
        assertItemOutput(helper, scrambled, item("gt6food:scrambled_egg"), 1);
        var flour = GTItems.getStack(MaterialPrefix.dust, ImportedMaterialData.Wheat);
        var dough = recipe(MachineRecipeMaps.Mixer, List.of(scrambled.itemOutputs[0], flour), List.of(), "egg dough");
        assertItemOutput(helper, dough, item("gt6food:egg_dough"), 1);

        var cooked = recipe(MachineRecipeMaps.Bath, List.of(raw), List.of(fluid("gt6ce:watergeothermal", 100)), "boiled egg");
        var boiled = item("gt6food:boiled_egg_2");
        helper.assertTrue(contains(cooked, boiled) && contains(cooked, Items.BONE_MEAL),
                "Geothermal water cooks the addon egg and recovers the shell as bonemeal");
        assertFluidOutput(helper, cooked, "minecraft:water", 100);
        var sliced = recipe(MachineRecipeMaps.Slicer,
                List.of(new ItemStack(boiled), stack("gt6ce:slicer_shape_flat")), List.of(), "sliced egg");
        assertItemOutput(helper, sliced, item("gt6food:sliced_egg"), 4);
        helper.assertTrue(sliced.isCatalystInput(1), "Slicing an egg retains the flat blade");

        var core = recipe(MachineRecipeMaps.Centrifuge, List.of(new ItemStack(Items.EGG)), List.of(), "core egg separation");
        helper.assertTrue(contains(core, item("gt6ce:egg_white")) && contains(core, item("gt6ce:egg_yolk")),
                "The existing vanilla egg centrifuge recipe still produces core egg parts");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void mixAndBottleMayonnaise(GameTestHelper helper) {
        var circuit = item("gt6ce:integrated_circuit_0");
        var mayo = recipe(MachineRecipeMaps.Mixer, List.of(stack("gt6food:egg_yolk"), new ItemStack(circuit)),
                List.of(fluid("gt6ce:seedoil", 100), fluid("gt6food:apple_cider_vinegar", 100)), "mayonnaise");
        assertFluidOutput(helper, mayo, "gt6food:mayo", 250);
        helper.assertTrue(retains(mayo, circuit), "The Mixer retains the circuit while making mayonnaise");
        assertBottleRoundTrip(helper, "gt6food:mayo");
        helper.succeed();
    }

    private static void assertBottleRoundTrip(GameTestHelper helper, String id) {
        var filled = recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(Items.GLASS_BOTTLE)),
                List.of(fluid(id, 250)), "bottled " + id);
        assertItemOutput(helper, filled, item(id), 1);
        helper.assertTrue(filled.fluidInputs.length == 1 && filled.fluidInputs[0].getAmount() == 250,
                "Bottling " + id + " consumes one 250 mB portion");

        var drained = recipe(MachineRecipeMaps.Canner, List.of(filled.itemOutputs[0]), List.of(), "drained " + id);
        assertFluidOutput(helper, drained, id, 250);
        assertItemOutput(helper, drained, Items.GLASS_BOTTLE, 1);
    }

    private static boolean contains(Recipe recipe, Item item) {
        for (ItemStack output : recipe.itemOutputs) {
            if (output.is(item) && output.getCount() == 1) return true;
        }
        return false;
    }

    private static boolean retains(Recipe recipe, Item item) {
        for (int index = 0; index < recipe.itemInputs.length; index++) {
            if (recipe.itemInputs[index].is(item)) return recipe.isCatalystInput(index);
        }
        return false;
    }

    private static void assertItemOutput(GameTestHelper helper, Recipe recipe, Item item, int count) {
        helper.assertTrue(recipe.itemOutputs.length == 1 && recipe.itemOutputs[0].is(item)
                        && recipe.itemOutputs[0].getCount() == count,
                "Expected " + count + " of " + ForgeRegistries.ITEMS.getKey(item));
    }

    private static void assertFluidOutput(GameTestHelper helper, Recipe recipe, String id, int amount) {
        helper.assertTrue(recipe.fluidOutputs.length == 1 && recipe.fluidOutputs[0].isFluidEqual(fluid(id, amount))
                        && recipe.fluidOutputs[0].getAmount() == amount,
                "Expected " + amount + " mB of " + id);
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

    private BreakfastGameTests() {}
}

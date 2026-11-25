package gt6foodworks.drink;

import gt6foodworks.GT6Foodworks;
import gregtech.content.data.MachineRecipeMaps;
import gregtech.engine.recipe.Recipe;
import gregtech.engine.recipe.RecipeMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GT6Foodworks.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritDrinkGameTests {
    private SpiritDrinkGameTests() {}

    @GameTest(template = "test_empty")
    public static void distillFruitBrandiesAndLiquors(GameTestHelper helper) {
        var circuit = List.of(circuit(0));
        var cider = produce(helper, MachineRecipeMaps.Fermenter, "cider", 25, circuit, fluid("apple_juice", 50));
        var appleBrandy = distill(helper, "apple_brandy", cider, 0);
        distill(helper, "apple_liquor", appleBrandy, 1);

        for (String fruit : List.of("apricot", "cherry", "elderberry", "pear", "plum")) {
            var wine = produce(helper, MachineRecipeMaps.Fermenter, fruit + "_wine", 250,
                    circuit, fluid(fruit + "_juice", 500));
            var brandy = distill(helper, fruit + "_brandy", wine, 0);
            if (!fruit.equals("plum")) {
                distill(helper, fruit + "_liquor", brandy, 1);
            }
        }

        var citrusMust = produce(helper, MachineRecipeMaps.Mixer, "citrus_must", 500, List.of(),
                fluid("orange_juice", 250), fluid("lemon_juice", 250));
        var citrusWine = produce(helper, MachineRecipeMaps.Fermenter, "citrus_wine", 250, circuit,
                citrusMust.fluidOutputs[0]);
        distill(helper, "citrus_brandy", citrusWine, 0);

        var fruitMust = produce(helper, MachineRecipeMaps.Mixer, "fruit_must", 500, List.of(),
                fluid("peach_juice", 250), fluid("apple_juice", 250));
        var fruitWine = produce(helper, MachineRecipeMaps.Fermenter, "fruit_wine", 250, circuit,
                fruitMust.fluidOutputs[0]);
        var fruitBrandy = distill(helper, "fruit_brandy", fruitWine, 0);
        distill(helper, "fruit_liquor", fruitBrandy, 1);

        for (String wine : List.of("red_wine", "white_wine", "wine", "ricardo_sanchez")) {
            produce(helper, MachineRecipeMaps.Distillery, "grape_brandy", 250, circuit, fluid(wine, 500));
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void distillCaneSpiritsGinAndTequila(GameTestHelper helper) {
        var rum = produce(helper, MachineRecipeMaps.Fermenter, "rum", 25, List.of(circuit(0)), fluid("reedwater", 50));
        var rumBatch = new FluidStack(rum.fluidOutputs[0], 500);
        produce(helper, MachineRecipeMaps.Distillery, "pirate_brew", 1, List.of(circuit(0)), rumBatch);
        var caneSpirit = distill(helper, "sugar_cane_spirit", rum, 1);
        var neutralSpirit = distill(helper, "neutral_spirit", caneSpirit, 0);
        var gin = produce(helper, MachineRecipeMaps.Distillery, "gin", 250, List.of(stack("gt6food:juniper_berries")),
                new FluidStack(neutralSpirit.fluidOutputs[0], 500));
        helper.assertTrue(!gin.isCatalystInput(0), "Gin consumes juniper berries");

        var agaveWine = produce(helper, MachineRecipeMaps.Fermenter, "agave_wine", 250, List.of(circuit(0)),
                fluid("agave_juice", 500));
        distill(helper, "tequila", agaveWine, 0);
        helper.succeed();
    }

    private static Recipe distill(GameTestHelper helper, String output, Recipe source, int setting) {
        var recipe = produce(helper, MachineRecipeMaps.Distillery, output, 250, List.of(circuit(setting)),
                new FluidStack(source.fluidOutputs[0], 500));
        helper.assertTrue(recipe.isCatalystInput(0), "Distillery keeps the selector circuit for " + output);
        return recipe;
    }

    private static Recipe produce(GameTestHelper helper, RecipeMap map, String output, int amount,
                                  List<ItemStack> items, FluidStack... input) {
        var recipe = map.findRecipe(items, List.of(input), true, items.size(), 4);
        helper.assertTrue(recipe != null && recipe.fluidOutputs.length == 1
                && recipe.fluidOutputs[0].isFluidStackIdentical(fluid(output, amount)), "Produces " + output);
        return recipe;
    }

    private static ItemStack circuit(int setting) {
        return stack("gt6ce:integrated_circuit_" + setting);
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id));
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(
                ResourceLocation.fromNamespaceAndPath(GT6Foodworks.MOD_ID, id)), id), amount);
    }
}

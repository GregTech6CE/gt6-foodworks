package gregtechfood;

import gregtech.content.data.ImportedMaterialData;
import gregtech.content.data.MachineRecipeMaps;
import gregtech.content.data.MaterialPrefix;
import gregtech.content.registry.GTItems;
import gregtech.engine.recipe.Recipe;
import gregtech.engine.recipe.RecipeMap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GregTechFood.MOD_ID)
@PrefixGameTestTemplate(false)
public final class IngredientGameTests {
    @GameTest(template = "test_empty")
    public static void processNewFruitIntoDesserts(GameTestHelper helper) {
        var grapes = stack("purple_grapes");
        var pressed = recipe(MachineRecipeMaps.Juicer, List.of(grapes), List.of(), "purple grape juice");
        helper.assertTrue(pressed.fluidOutputs.length == 1
                        && pressed.fluidOutputs[0].isFluidStackIdentical(fluid("grape_juice_4", 100)),
                "Purple grapes yield 100 mB of purple grape juice in the Juicer");
        var dried = recipe(MachineRecipeMaps.Drying, List.of(grapes), List.of(), "purple raisins");
        assertOutput(helper, dried, "purple_raisins");
        var coated = recipe(MachineRecipeMaps.Bath, List.of(dried.itemOutputs[0]),
                List.of(fluid("gt6ce:molten_chocolate", 36)), "chocolate raisins");
        assertOutput(helper, coated, "chocolate_raisins");
        var dough = recipe(MachineRecipeMaps.Mixer, List.of(stack("sugary_dough"), coated.itemOutputs[0]),
                List.of(), "chocolate raisin dough");
        assertOutput(helper, dough, "sugary_chocolate_raisin_dough");

        var iceCream = stack("ice_cream");
        var cherry = recipe(MachineRecipeMaps.Mixer, List.of(iceCream), List.of(fluid("cherry_juice", 50)),
                "cherry ice cream");
        var kiwi = recipe(MachineRecipeMaps.Mixer, List.of(iceCream), List.of(fluid("kiwi_juice", 50)),
                "kiwi ice cream");
        assertOutput(helper, cherry, "cherry_ice_cream");
        assertOutput(helper, kiwi, "kiwi_ice_cream");

        var groundVanilla = recipe(MachineRecipeMaps.Mortar, List.of(stack("vanilla_bean")), List.of(), "ground vanilla");
        var vanilla = recipe(MachineRecipeMaps.Mixer, List.of(iceCream, groundVanilla.itemOutputs[0]), List.of(),
                "vanilla ice cream");
        assertOutput(helper, vanilla, "vanilla_ice_cream");
        var spumoni = recipe(MachineRecipeMaps.Mixer,
                List.of(cherry.itemOutputs[0], stack("pistachio_ice_cream"), vanilla.itemOutputs[0]), List.of(),
                "Spumoni vanilla ice cream");
        assertOutput(helper, spumoni, "spumoni_vanilla_ice_cream");

        var peanutButter = recipe(MachineRecipeMaps.Mixer, List.of(stack("peanut")),
                List.of(fluid("heavy_cream", 250)), "peanut butter");
        var peanutButterIceCream = recipe(MachineRecipeMaps.Mixer, List.of(iceCream),
                List.of(peanutButter.fluidOutputs[0]), "peanut butter ice cream");
        assertOutput(helper, peanutButterIceCream, "peanut_butter_ice_cream");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void processPicklesAndSeasonSavoryFood(GameTestHelper helper) {
        var vinegar = fluid("apple_cider_vinegar", 125);
        var pickle = recipe(MachineRecipeMaps.Bath, List.of(stack("cucumber")), List.of(vinegar), "pickle");
        assertOutput(helper, pickle, "pickle");
        var blade = stack("gt6ce:slicer_shape_flat");
        var sliced = recipe(MachineRecipeMaps.Slicer, List.of(pickle.itemOutputs[0], blade), List.of(), "pickle slices");
        assertOutput(helper, sliced, "pickle_slice");
        var carrots = recipe(MachineRecipeMaps.Slicer, List.of(new ItemStack(Items.CARROT), blade),
                List.of(), "carrot slices");
        assertOutput(helper, carrots, "carrot_slice");

        var chili = recipe(MachineRecipeMaps.Juicer, List.of(stack("chili_pepper")), List.of(), "chili");
        helper.assertTrue(chili.fluidOutputs.length == 1
                        && chili.fluidOutputs[0].isFluidStackIdentical(fluid("chili_sauce", 75)),
                "A chili pepper yields 75 mB of chili sauce in the Juicer");
        var seasoned = recipe(MachineRecipeMaps.Mixer, List.of(stack("potato_chips"), chili.itemOutputs[0]),
                List.of(), "chili chips");
        assertOutput(helper, seasoned, "chili_chips");

        var pepper = recipe(MachineRecipeMaps.Mortar, List.of(stack("black_pepper")), List.of(), "ground black pepper");
        assertOutput(helper, pepper, "ground_black_pepper");
        var salt = GTItems.getStack(MaterialPrefix.dust, ImportedMaterialData.NaCl);
        var sauce = recipe(MachineRecipeMaps.Mixer, List.of(salt, pepper.itemOutputs[0]),
                List.of(fluid("tomato_ketchup", 250), vinegar), "barbecue sauce");
        helper.assertTrue(sauce.fluidOutputs.length == 1
                        && sauce.fluidOutputs[0].isFluidStackIdentical(fluid("barbecue_sauce", 500)),
                "The Mixer produces 500 mB of barbecue sauce");
        assertOutput(helper, recipe(MachineRecipeMaps.Canner, List.of(new ItemStack(Items.GLASS_BOTTLE)),
                List.of(sauce.fluidOutputs[0]), "bottled barbecue sauce"), "barbecue_sauce");
        assertOutput(helper, recipe(MachineRecipeMaps.Bath, List.of(stack("grilled_ribs")),
                List.of(sauce.fluidOutputs[0]), "barbecue ribs"), "barbecue_ribs");
        helper.succeed();
    }

    private static void assertOutput(GameTestHelper helper, Recipe recipe, String output) {
        var expected = stack(output).getItem();
        helper.assertTrue(Arrays.stream(recipe.itemOutputs).anyMatch(stack -> stack.is(expected)),
                "Missing item output " + output);
    }

    private static Recipe recipe(RecipeMap map, List<ItemStack> items, List<FluidStack> fluids, String name) {
        return Objects.requireNonNull(map.findRecipe(items, fluids, true, items.size(), 4), "Missing recipe for " + name);
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location(id)), id));
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(location(id)), id), amount);
    }

    private static ResourceLocation location(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID, id);
    }

    private IngredientGameTests() {}
}

package gregtechfood;

import gregtech.content.data.MachineRecipeMaps;
import gregtech.engine.recipe.Recipe;
import gregtech.engine.recipe.RecipeMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GregTechFood.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BeerGameTests {
    @GameTest(template = "test_empty")
    public static void brewAllBeerStyles(GameTestHelper helper) {
        var yeast = craftYeast(helper);
        var coldCulture = recipe(MachineRecipeMaps.Mixer, List.of(yeast), List.of(fluid("gt6ce:ice", 125)),
                "Lager yeast");
        var lagerYeast = assertItemOutput(helper, coldCulture, "lager_yeast");

        var dried = recipe(MachineRecipeMaps.Drying, List.of(item("barley", 1)), List.of(), "Barley malt");
        var malt = assertItemOutput(helper, dried, "barley_malt");
        var roasted = recipe(MachineRecipeMaps.Roasting, List.of(malt), List.of(fluid("gt6ce:air", 250)),
                "Roasted malt");
        var darkMalt = assertItemOutput(helper, roasted, "roasted_barley_malt");

        var barleyWort = mash(helper, malt.copyWithCount(2), "barley_wort");
        assertFerments(helper, barleyWort, yeast, "ale");
        assertFerments(helper, barleyWort, lagerYeast, "lager");
        assertFerments(helper, mash(helper, item("corn", 2), "corn_wort"), yeast, "corn_beer");
        assertFerments(helper, mash(helper, item("rye", 2), "rye_wort"), yeast, "rye_beer");
        assertFerments(helper, mash(helper, darkMalt.copyWithCount(2), "roasted_barley_wort"), yeast, "stout");
        assertFerments(helper, mash(helper, new ItemStack(Items.WHEAT, 2), "wheat_wort"), yeast, "wheat_beer");
        helper.succeed();
    }

    private static ItemStack craftYeast(GameTestHelper helper) {
        var level = helper.getLevel();
        var menu = new CraftingMenu(0, helper.makeMockPlayer().getInventory());
        var grid = new TransientCraftingContainer(menu, 2, 2);
        grid.setItem(0, new ItemStack(Items.BROWN_MUSHROOM));
        grid.setItem(1, new ItemStack(Items.BREAD));
        var yeast = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level)
                .orElseThrow(() -> new IllegalStateException("Missing brewing yeast recipe"))
                .assemble(grid, level.registryAccess());
        helper.assertTrue(yeast.is(item("brewing_yeast", 1).getItem()) && yeast.getCount() == 4,
                "Mushroom and bread yield four brewing yeast");
        return yeast.copyWithCount(1);
    }

    private static FluidStack mash(GameTestHelper helper, ItemStack grain, String expected) {
        var result = recipe(MachineRecipeMaps.Mixer, List.of(grain, item("hops", 1)),
                List.of(fluid("minecraft:water", 1000)), expected);
        return assertFluidOutput(helper, result, expected);
    }

    private static void assertFerments(GameTestHelper helper, FluidStack wort, ItemStack yeast, String expected) {
        var result = recipe(MachineRecipeMaps.Fermenter, List.of(yeast), List.of(wort), expected);
        assertFluidOutput(helper, result, expected);
    }

    private static Recipe recipe(RecipeMap map, List<ItemStack> items, List<FluidStack> fluids, String name) {
        return Objects.requireNonNull(map.findRecipe(items, fluids, true, items.size(), 4),
                "Missing recipe for " + name);
    }

    private static ItemStack assertItemOutput(GameTestHelper helper, Recipe recipe, String id) {
        helper.assertTrue(recipe.itemOutputs.length == 1 && recipe.itemOutputs[0].is(item(id, 1).getItem()),
                "Expected item output " + id);
        return recipe.itemOutputs[0];
    }

    private static FluidStack assertFluidOutput(GameTestHelper helper, Recipe recipe, String id) {
        helper.assertTrue(recipe.fluidOutputs.length == 1
                && recipe.fluidOutputs[0].isFluidStackIdentical(fluid("gt6food:" + id, 1000)),
                "Expected 1000 mB of " + id);
        return recipe.fluidOutputs[0];
    }

    private static ItemStack item(String id, int amount) {
        var key = ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID, id);
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(key), id), amount);
    }

    private static FluidStack fluid(String id, int amount) {
        var key = ResourceLocation.parse(id);
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(key), id), amount);
    }

    private BeerGameTests() {}
}

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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("gt6food")
@PrefixGameTestTemplate(false)
public final class HandheldFoodGameTests {
    @GameTest(template = "test_empty")
    public static void craftFromAddonIngredients(GameTestHelper helper) {
        assertCrafts(helper, "cheese_burger", addon("buns"), addon("cheese_slice"), addon("cheese_slice"),
                addon("cheese_slice"));
        assertCrafts(helper, "bacon_sandwich", addon("breads"), addon("grilled_bacon"), addon("grilled_bacon"),
                addon("grilled_bacon"));
        assertCrafts(helper, "large_veggie_sandwich", addon("sliced_baguette"), addon("sliced_baguette"),
                addon("cucumber_slice"), addon("cucumber_slice"), addon("cucumber_slice"),
                addon("tomato_slice"), addon("tomato_slice"), addon("tomato_slice"), addon("onion_slice"));

        var replicated = findRecipe(MachineRecipeMaps.Replicator,
                List.of(stack("gt6ce:integrated_circuit_7"), stack("gt6ce:integrated_circuit_9")),
                List.of(fluid("gt6ce:neutralmatter", 1), fluid("gt6ce:chargedmatter", 1), fluid("gt6ce:biomass", 1000)),
                "Emerald Green Bar");
        assertOutput(helper, replicated, "emerald_green_bar", 1);
        assertCrafts(helper, "soylent_burger", addon("buns"), replicated.itemOutputs[0].copy());
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void processAddonChum(GameTestHelper helper) {
        var squeezed = findRecipe(MachineRecipeMaps.Squeezer, List.of(new ItemStack(Items.COD)), List.of(), "fish oil");
        var oil = fluid("gt6ce:fishoil", 1000);
        helper.assertTrue(squeezed.fluidOutputs.length == 1 && squeezed.fluidOutputs[0].isFluidEqual(oil)
                        && squeezed.fluidOutputs[0].getAmount() >= oil.getAmount(),
                "The Squeezer extracts fish oil from cod");
        var rottenMeat = GTItems.getStack(MaterialPrefix.dust, ImportedMaterialData.MeatRotten, 1);
        var mixed = findRecipe(MachineRecipeMaps.Mixer,
                List.of(addon("raw_ham_slice"), new ItemStack(Items.FERMENTED_SPIDER_EYE), rottenMeat,
                        new ItemStack(Items.BONE_MEAL), new ItemStack(Items.POISONOUS_POTATO),
                        new ItemStack(Items.RED_MUSHROOM)), List.of(fluid("gt6food:purple_drink", 1000), oil), "Chum");
        assertOutput(helper, mixed, "chum", 8);
        assertCrafts(helper, "chum_burger", addon("buns"), addon("chum"));
        var canned = findRecipe(MachineRecipeMaps.Canner, List.of(addon("chum"), addon("empty_food_can")),
                List.of(), "canned Chum");
        assertOutput(helper, canned, "canned_chum", 1);

        var player = helper.makeMockPlayer();
        var burger = addon("chum_burger");
        burger.finishUsingItem(helper.getLevel(), player);
        var hunger = player.getEffect(MobEffects.HUNGER);
        helper.assertTrue(hunger != null && hunger.getAmplifier() == 4 && hunger.getDuration() == 1000,
                "Eating a Chum Burger applies its Hunger effect");
        helper.succeed();
    }

    private static void assertCrafts(GameTestHelper helper, String expected, ItemStack... ingredients) {
        var level = helper.getLevel();
        var grid = new TransientCraftingContainer(new CraftingMenu(0, helper.makeMockPlayer().getInventory()), 3, 3);
        for (int i = 0; i < ingredients.length; i++) grid.setItem(i, ingredients[i]);
        var crafted = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level)
                .orElseThrow(() -> new IllegalStateException("Missing crafting recipe for " + expected))
                .assemble(grid, level.registryAccess());
        helper.assertTrue(crafted.is(addon(expected).getItem()) && crafted.getCount() == 1,
                "Expected one " + expected);
    }

    private static void assertOutput(GameTestHelper helper, Recipe recipe, String expected, int count) {
        helper.assertTrue(recipe.itemOutputs.length == 1 && recipe.itemOutputs[0].is(addon(expected).getItem())
                        && recipe.itemOutputs[0].getCount() == count, "Expected " + count + " " + expected);
    }

    private static Recipe findRecipe(RecipeMap map, List<ItemStack> items, List<FluidStack> fluids, String name) {
        return Objects.requireNonNull(map.findRecipe(items, fluids, true, items.size(), 4), "Missing recipe for " + name);
    }

    private static ItemStack addon(String id) {
        return stack("gt6food:" + id);
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id));
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(id)), id), amount);
    }

    private HandheldFoodGameTests() {}
}

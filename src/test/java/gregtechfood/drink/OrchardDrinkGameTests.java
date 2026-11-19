package gregtechfood.drink;

import gregtechfood.GregTechFood;
import gregtech.content.data.MachineRecipeMaps;
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

@GameTestHolder(GregTechFood.MOD_ID)
@PrefixGameTestTemplate(false)
public final class OrchardDrinkGameTests {
    private static final List<FruitPress> NEW_FRUITS = List.of(
            new FruitPress("lime", 125, 100),
            new FruitPress("grapefruit", 400, 350),
            new FruitPress("persimmon", 125, 100),
            new FruitPress("starfruit", 100, 75),
            new FruitPress("fig", 100, 75),
            new FruitPress("mango", 150, 125),
            new FruitPress("papaya", 400, 350));
    private static final List<String> BREWING_FRUITS = List.of("apricot", "orange", "elderberry", "peach", "pear", "plum");

    @GameTest(template = "test_empty")
    public static void pressNewFruitAndMixSmoothies(GameTestHelper helper) {
        for (var fruit : NEW_FRUITS) {
            String juice = fruit.id() + "_juice";
            var item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(addon(fruit.id())), fruit.id());
            var produce = List.of(new ItemStack(item));
            var pressed = machineOutput(helper, MachineRecipeMaps.Squeezer, produce, List.of(),
                    juice, fruit.squeezerAmount());
            machineOutput(helper, MachineRecipeMaps.Juicer, produce, List.of(), juice, fruit.juicerAmount());
            mixSmoothie(helper, pressed, fruit.id() + "_smoothie");
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void mixBrewingFruitSmoothiesAndFruitBlend(GameTestHelper helper) {
        for (String fruit : BREWING_FRUITS) {
            var item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(addon(fruit)), fruit);
            var juice = machineOutput(helper, MachineRecipeMaps.Squeezer, List.of(new ItemStack(item)), List.of(),
                    fruit + "_juice", fruit.equals("elderberry") ? 125 : 150);
            mixSmoothie(helper, juice, fruit + "_smoothie");
        }

        var juice = machineOutput(helper, MachineRecipeMaps.Mixer, List.of(),
                List.of(fluid(addon("apple_juice"), 125), fluid(addon("melon_juice"), 125)), "juice", 250);
        mixSmoothie(helper, juice, "froot_smoothie");
        helper.succeed();
    }

    private static void mixSmoothie(GameTestHelper helper, FluidStack juice, String result) {
        machineOutput(helper, MachineRecipeMaps.Mixer, List.of(),
                List.of(fluid(ResourceLocation.parse("gt6ce:ice"), 125), new FluidStack(juice, 125)), result, 250);
    }

    private static FluidStack machineOutput(GameTestHelper helper, RecipeMap machine, List<ItemStack> items,
                                           List<FluidStack> fluids, String result, int amount) {
        var recipe = machine.findRecipe(items, fluids, true, items.size(), 4);
        helper.assertTrue(recipe != null && recipe.fluidOutputs.length == 1
                        && recipe.fluidOutputs[0].isFluidStackIdentical(fluid(addon(result), amount)),
                "The machine produces " + amount + " mB of " + result);
        return recipe.fluidOutputs[0];
    }

    private static ResourceLocation addon(String id) {
        return ResourceLocation.fromNamespaceAndPath(GregTechFood.MOD_ID, id);
    }

    private static FluidStack fluid(ResourceLocation id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(id), id.toString()), amount);
    }

    private record FruitPress(String id, int squeezerAmount, int juicerAmount) {}

    private OrchardDrinkGameTests() {}
}

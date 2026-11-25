package gt6foodworks.drink;

import gt6foodworks.GT6Foodworks;
import gregtech.content.data.MachineRecipeMaps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GT6Foodworks.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LiqueurGameTests {
    private LiqueurGameTests() {}

    @GameTest(template = "test_empty")
    public static void mixAllLiqueurs(GameTestHelper helper) {
        for (String fruit : List.of("banana", "blackberry", "cherry", "lemon", "melon", "orange", "peach", "raspberry")) {
            assertMix(helper, fruit, "gt6food:" + fruit + "_juice", List.of());
        }
        assertMix(helper, "chocolate", "gt6ce:molten_chocolate", List.of());
        assertMix(helper, "coffee", "gt6food:dark_coffee", List.of());
        assertMix(helper, "almond", "minecraft:water", List.of("almond"));
        assertMix(helper, "anise", "minecraft:water", List.of("anise"));
        assertMix(helper, "blackcurrant", "minecraft:water", List.of("black_currants"));
        assertMix(helper, "cinnamon", "minecraft:water", List.of("cinnamon_bark"));
        assertMix(helper, "hazelnut", "minecraft:water", List.of("hazelnut"));
        assertMix(helper, "mint", "minecraft:water", List.of("mint_leaves"));
        assertMix(helper, "herbal", "gt6food:tea", List.of("mint_leaves"));
        helper.succeed();
    }

    private static void assertMix(GameTestHelper helper, String flavor, String flavorFluid, List<String> flavorItems) {
        var ingredients = new ArrayList<ItemStack>();
        ingredients.add(new ItemStack(Items.SUGAR));
        for (String input : flavorItems) {
            ingredients.add(new ItemStack(Objects.requireNonNull(
                    ForgeRegistries.ITEMS.getValue(addonId(input)), input)));
        }
        var fluids = List.of(fluid(addonId("neutral_spirit"), 125), fluid(ResourceLocation.parse(flavorFluid), 125));
        var recipe = MachineRecipeMaps.Mixer.findRecipe(ingredients, fluids, true, ingredients.size(), 4);
        String name = flavor + "_liqueur";
        ResourceLocation id = addonId(name);
        helper.assertTrue(recipe != null && recipe.fluidOutputs.length == 1
                && recipe.fluidOutputs[0].isFluidStackIdentical(fluid(id, 250)), "Expected Mixer recipe for " + name);

        var bottle = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id), name);
        var food = bottle.getFoodProperties();
        helper.assertTrue(bottle instanceof BottledFoodItem && food != null && food.getNutrition() == 4
                && food.getEffects().stream().anyMatch(effect -> effect.getFirst().getEffect() == MobEffects.DAMAGE_RESISTANCE
                && effect.getFirst().getAmplifier() == 1 && effect.getFirst().getDuration() == 400
                && effect.getSecond() == 0.6F), "Expected bottled nutrition and resistance for " + name);
    }

    private static ResourceLocation addonId(String path) {
        return ResourceLocation.fromNamespaceAndPath(GT6Foodworks.MOD_ID, path);
    }

    private static FluidStack fluid(ResourceLocation id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(id), id.toString()), amount);
    }
}

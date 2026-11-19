package gregtechfood;

import gregtech.content.data.MachineRecipeMaps;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("gt6food")
@PrefixGameTestTemplate(false)
public final class CanningGameTests {
    @GameTest(template = "test_empty")
    public static void packNamedProduceAndBakedFoods(GameTestHelper helper) {
        checkCanning(helper, new ItemStack(Items.APPLE), "canned_apples", 4);
        checkCanning(helper, addon("pickle", 1), "canned_pickles", 2);
        checkCanning(helper, addon("sliced_bun", 2), "tiny_food_can_bread", 2);
        checkCanning(helper, addon("baguette", 1), "canned_baguette", 8);
        checkCanning(helper, addon("raisin_cookie", 6), "raisin_cookie_tin", 12);
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void packNamedMeatAndFish(GameTestHelper helper) {
        checkCanning(helper, new ItemStack(Items.BEEF), "canned_raw_beef", 2);
        checkCanning(helper, new ItemStack(Items.COOKED_BEEF), "canned_beef", 8);
        checkCanning(helper, new ItemStack(Items.COOKED_SALMON), "canned_salmon", 6);
        checkCanning(helper, new ItemStack(Items.COD), "canned_raw_cod", 2);
        ItemStack ham = checkCanning(helper, addon("cooked_ham", 1), "canned_ham", 10);
        checkCanning(helper, addon("barbecue_ribs", 1), "canned_ribs", 10);
        checkCanning(helper, addon("grilled_mule_meat", 1), "canned_mule", 10);
        checkCanning(helper, addon("emerald_green_bar", 1), "canned_emerald_green", 2);
        checkCanning(helper, addon("chum", 1), "canned_chum", 4);

        for (String id : List.of("canned_ham", "canned_salmon", "canned_emerald_green", "canned_chum")) {
            helper.assertTrue(SandwichLayers.forItem(addon(id, 1)) != null, "Missing sandwich layer for " + id);
        }

        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        var wolf = Objects.requireNonNull(EntityType.WOLF.create(helper.getLevel()));
        wolf.setTame(true);
        wolf.setHealth(5.0F);
        helper.assertTrue(ham.interactLivingEntity(player, wolf, InteractionHand.MAIN_HAND).consumesAction()
                        && wolf.getHealth() == 15.0F && ham.isEmpty()
                        && player.getInventory().countItem(addon("empty_food_can", 1).getItem()) == 1,
                "Named meat cans must heal tamed wolves and return their empty can");
        helper.succeed();
    }

    private static ItemStack checkCanning(GameTestHelper helper, ItemStack food, String outputId, int nutrition) {
        ItemStack can = addon("empty_food_can", 1);
        var recipe = Objects.requireNonNull(MachineRecipeMaps.Canner.findRecipe(
                List.of(food, can), List.of(), true, 2, 4), "Missing canning recipe for " + food);
        helper.assertTrue(recipe.itemOutputs.length == 1, "Expected one can output for " + food);
        var result = recipe.itemOutputs[0];
        helper.assertTrue(result.is(addon(outputId, 1).getItem()) && result.getCount() == 1
                        && result.getCraftingRemainingItem().is(can.getItem())
                        && result.getFoodProperties(null).getNutrition() == nutrition,
                "Expected " + outputId + " to return an empty can and provide " + nutrition + " hunger points");
        return result.copy();
    }

    private static ItemStack addon(String id, int count) {
        var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("gt6food", id));
        return new ItemStack(Objects.requireNonNull(item, id), count);
    }

    private CanningGameTests() {}
}

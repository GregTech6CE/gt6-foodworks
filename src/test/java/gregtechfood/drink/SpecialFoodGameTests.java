package gregtechfood.drink;

import gregtechfood.GregTechFood;
import gregtech.content.data.MachineRecipeMaps;
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
import net.minecraft.world.entity.EntityType;
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
public final class SpecialFoodGameTests {
    @GameTest(template = "test_empty")
    public static void makeSpecialDrinks(GameTestHelper helper) {
        assertProduces(helper, MachineRecipeMaps.Fermenter, "rotten_drink",
                List.of(new ItemStack(Items.ROTTEN_FLESH)), List.of(fluid("gt6food:apple_juice", 250)));
        assertProduces(helper, MachineRecipeMaps.Injector, "holy_water",
                List.of(new ItemStack(Items.GLOWSTONE_DUST)), List.of(fluid("minecraft:water", 250)));
        assertProduces(helper, MachineRecipeMaps.Injector, "bawls", List.of(),
                List.of(fluid("gt6ce:slime", 250), fluid("gt6ce:carbondioxide", 250)));
        assertProduces(helper, MachineRecipeMaps.Mixer, "medicine", List.of(),
                List.of(fluid("gt6food:rainbow_sap", 125), fluid("gt6food:ambrosia", 125)));
        var circuit = List.of(stack("gt6ce:integrated_circuit_2"));
        assertProduces(helper, MachineRecipeMaps.Mixer, "laxative", circuit,
                List.of(fluid("gt6food:apple_juice", 125), fluid("gt6food:plum_juice", 125)));
        for (String fruit : List.of("apple", "plum")) {
            for (String grape : List.of("grape_juice", "grape_juice_2", "grape_juice_3", "grape_juice_4")) {
                assertProduces(helper, MachineRecipeMaps.Mixer, "laxative", circuit,
                        List.of(fluid("gt6food:" + fruit + "_juice", 125), fluid("gt6food:" + grape, 125)));
            }
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void makeAndUsePills(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getAbilities().invulnerable = false;
        player.getFoodData().setFoodLevel(20);
        var wax = stack("gt6ce:empty_wax_pill");

        assertPacks(helper, wax, stack("gt6food:mint_leaves"), "peppermint");
        assertPacks(helper, wax, stack("gt6food:spice_leaf"), "blue_pill");
        assertPacks(helper, wax, new ItemStack(Items.CARROT), "red_pill");

        var level = helper.getLevel();
        var grid = new TransientCraftingContainer(new CraftingMenu(0, player.getInventory()), 2, 2);
        grid.setItem(0, wax);
        grid.setItem(1, new ItemStack(Items.BROWN_MUSHROOM));
        var antidote = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level)
                .orElseThrow(() -> new IllegalStateException("Missing addon antidote recipe"))
                .assemble(grid, level.registryAccess());
        helper.assertTrue(antidote.is(stack("gt6food:antidote").getItem()) && antidote.getCount() == 1,
                "The workbench fills an empty wax pill with a brown mushroom");
        var classic = MachineRecipeMaps.Boxinator.findRecipe(List.of(wax, grid.getItem(1)), List.of(), true, 2, 4);
        helper.assertTrue(classic != null && classic.itemOutputs[0].is(stack("gt6ce:antidote").getItem()),
                "The original Boxinator antidote recipe is still available");

        var pills = List.of(stack("gt6food:peppermint"), stack("gt6food:blue_pill"), stack("gt6food:red_pill"), antidote);
        var effects = List.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.CONFUSION, MobEffects.BLINDNESS, MobEffects.POISON);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200));
        for (int i = 0; i < pills.size(); i++) {
            var effect = effects.get(i);
            player.addEffect(new MobEffectInstance(effect, 200));
            var pill = pills.get(i);
            player.setItemInHand(InteractionHand.MAIN_HAND, pill);
            helper.assertTrue(pill.getItem().use(level, player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                    "Pills can be eaten with a full hunger bar");
            helper.assertTrue(pill.finishUsingItem(level, player).isEmpty() && !player.hasEffect(effect),
                    "Each pill is consumed and removes its intended effect");
            player.stopUsingItem();
        }
        helper.assertTrue(player.hasEffect(MobEffects.DAMAGE_RESISTANCE), "Pills leave unrelated effects intact");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void useHolyWaterOnWeakZombieVillager(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        var zombie = Objects.requireNonNull(EntityType.ZOMBIE_VILLAGER.create(level));
        var bottle = stack("gt6food:holy_water");
        player.setItemInHand(InteractionHand.MAIN_HAND, bottle);
        helper.assertTrue(player.interactOn(zombie, InteractionHand.MAIN_HAND) == InteractionResult.PASS
                        && !zombie.isConverting() && bottle.getCount() == 1,
                "Holy water requires a weakened zombie villager");

        zombie.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100));
        helper.assertTrue(player.interactOn(zombie, InteractionHand.MAIN_HAND).consumesAction()
                        && zombie.isConverting() && player.getMainHandItem().is(Items.GLASS_BOTTLE),
                "Holy water starts the vanilla conversion process and gives back the glass bottle");

        var secondZombie = Objects.requireNonNull(EntityType.ZOMBIE_VILLAGER.create(level));
        secondZombie.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100));
        player.setItemInHand(InteractionHand.MAIN_HAND, bottle.copyWithCount(2));
        helper.assertTrue(player.interactOn(secondZombie, InteractionHand.MAIN_HAND).consumesAction()
                        && player.getMainHandItem().is(bottle.getItem()) && player.getMainHandItem().getCount() == 1
                        && player.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE)),
                "Using one bottle from a stack returns the empty bottle to inventory");
        helper.succeed();
    }

    private static void assertProduces(GameTestHelper helper, RecipeMap map, String drink,
                                       List<ItemStack> items, List<FluidStack> fluids) {
        var recipe = map.findRecipe(items, fluids, true, items.size(), 4);
        helper.assertTrue(recipe != null && recipe.fluidOutputs.length == 1
                        && recipe.fluidOutputs[0].isFluidStackIdentical(fluid("gt6food:" + drink, 250)),
                "Expected 250 mB of " + drink);
        if (drink.equals("laxative")) {
            helper.assertTrue(recipe.isCatalystInput(0), "The Mixer retains the laxative selector circuit");
        }
    }

    private static void assertPacks(GameTestHelper helper, ItemStack wax, ItemStack filling, String expected) {
        var recipe = MachineRecipeMaps.Boxinator.findRecipe(List.of(wax, filling), List.of(), true, 2, 4);
        helper.assertTrue(recipe != null && recipe.itemOutputs.length == 1
                        && recipe.itemOutputs[0].is(stack("gt6food:" + expected).getItem()),
                "The Boxinator produces " + expected + " from the selected filling");
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id));
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(id)), id),
                amount);
    }

    private SpecialFoodGameTests() {}
}

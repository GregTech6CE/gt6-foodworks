package gregtechfood;

import gregtech.content.data.ImportedMaterialData;
import gregtech.content.data.MachineRecipeMaps;
import gregtech.content.data.MaterialPrefix;
import gregtech.content.registry.GTItems;
import gregtech.engine.recipe.RecipeMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(GregTechFood.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DrinkMigrationGameTests {
    @GameTest(template = "test_empty")
    public static void produceDrinksFromSurvivalInputs(GameTestHelper helper) {
        var water = fluid("minecraft:water", 750);
        var mixer = MachineRecipeMaps.Mixer;
        var coffee = produce(helper, MachineRecipeMaps.Distillery, "dark_coffee", 750,
                List.of(stack("gt6food:coffee_beans")), water);
        produce(helper, mixer, "coffee", 250, List.of(),
                new FluidStack(coffee, 125), fluid("gt6food:milk", 125));
        var tea = produce(helper, mixer, "tea", 750, List.of(stack("gt6food:tea_leaves")), water);
        var sweetTea = produce(helper, mixer, "sweet_tea", 750, List.of(new ItemStack(Items.SUGAR)), tea);
        produce(helper, MachineRecipeMaps.CryoMixer, "iced_tea", 250, List.of(),
                new FluidStack(sweetTea, 125), fluid("gt6food:apple_juice", 125));

        var circuit = stack("gt6ce:integrated_circuit_1");
        var soda = produce(helper, MachineRecipeMaps.Injector, "soda", 250, List.of(circuit),
                fluid("minecraft:water", 250), fluid("gt6ce:carbondioxide", 250));
        helper.assertTrue(!soda.isEmpty(), "Carbonation selects the addon soda");
        var grapeWine = produce(helper, MachineRecipeMaps.Fermenter, "ricardo_sanchez", 25,
                List.of(stack("gt6ce:integrated_circuit_0")), fluid("gt6food:grape_juice_4", 50));
        produce(helper, MachineRecipeMaps.Fermenter, "grape_vinegar", 10,
                List.of(stack("gt6ce:integrated_circuit_0")), new FluidStack(grapeWine, 50));

        var honey = MachineRecipeMaps.Canner.findRecipe(List.of(new ItemStack(Items.HONEY_BOTTLE)), List.of(),
                true, 1, 4);
        helper.assertTrue(honey != null && honey.fluidOutputs[0].isFluidEqual(fluid("gt6ce:honey", 250)),
                "Honey collected in a vanilla bottle supplies the mead chain");
        var jelly = produce(helper, mixer, "royal_jelly", 100, List.of(new ItemStack(Items.HONEYCOMB)),
                fluid("gt6ce:honey", 250));
        var honeydew = produce(helper, mixer, "honeydew", 250, List.of(),
                fluid("gt6ce:honey", 125), fluid("gt6food:reedwater", 125));
        produce(helper, mixer, "ambrosia", 400, List.of(), jelly, new FluidStack(honeydew, 200));

        var maple = MachineRecipeMaps.Canner.findRecipe(List.of(new ItemStack(Items.GLASS_BOTTLE), circuit),
                List.of(fluid("gt6ce:maplesap", 250)), true, 2, 4);
        helper.assertTrue(maple != null && maple.itemOutputs[0].is(stack("gt6food:maple_sap").getItem()),
                "A circuit selects the addon bottle from GT6CE's tap fluid");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void brewDarkBeerDragonBloodAndSake(GameTestHelper helper) {
        var circuit = stack("gt6ce:integrated_circuit_0");
        var mixer = MachineRecipeMaps.Mixer;
        var fermenter = MachineRecipeMaps.Fermenter;
        var water = fluid("minecraft:water", 250);
        var hops = produce(helper, mixer, "hops_mash", 250,
                List.of(stack("gt6food:hops"), circuit), water);
        var darkBeer = produce(helper, fermenter, "dark_beer", 250,
                List.of(circuit), new FluidStack(hops, 500));
        produce(helper, fermenter, "dragon_blood", 50, List.of(circuit), darkBeer);
        var riceMash = produce(helper, mixer, "rice_mash", 250, List.of(stack("gt6food:rice")), water);
        produce(helper, fermenter, "sake", 250, List.of(circuit), new FluidStack(riceMash, 500));
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void makeSoyMilkDressingAndMushroomStew(GameTestHelper helper) {
        var soyMilk = produce(helper, MachineRecipeMaps.Squeezer, "soy_milk", 100,
                List.of(stack("gt6food:soybean")));
        produce(helper, MachineRecipeMaps.Centrifuge, "heavy_cream", 50, List.of(), new FluidStack(soyMilk, 50));
        var player = helper.makeMockPlayer();
        player.getAbilities().instabuild = false;
        player.getFoodData().setFoodLevel(20);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        var soyBottle = stack("gt6food:soy_milk");
        var returned = soyBottle.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(returned.is(Items.GLASS_BOTTLE) && !player.hasEffect(MobEffects.POISON),
                "Soy milk returns its glass and cleanses milk-curable effects");

        var salt = GTItems.getStack(MaterialPrefix.dust, ImportedMaterialData.NaCl);
        for (String vinegar : List.of("gt6food:apple_cider_vinegar", "gt6food:grape_vinegar", "gt6food:cane_vinegar")) {
            produce(helper, MachineRecipeMaps.Mixer, "dressing", 250, List.of(salt),
                    fluid("gt6ce:seedoil", 100), fluid(vinegar, 100));
        }
        var soup = MachineRecipeMaps.Mixer.findRecipe(
                List.of(new ItemStack(Items.RED_MUSHROOM), new ItemStack(Items.BROWN_MUSHROOM)), List.of(), true, 2, 4);
        var soupFluid = fluid("gt6ce:mushroomsoup", 1000);
        helper.assertTrue(soup != null && soup.fluidOutputs.length == 1
                        && soup.fluidOutputs[0].isFluidStackIdentical(soupFluid),
                "Mix vanilla mushrooms into the core soup fluid");
        var canner = MachineRecipeMaps.Canner;
        var filled = canner.findRecipe(List.of(new ItemStack(Items.BOWL)), List.of(soupFluid), true, 1, 4);
        var drained = canner.findRecipe(List.of(new ItemStack(Items.MUSHROOM_STEW)), List.of(), true, 1, 4);
        helper.assertTrue(filled != null && filled.itemOutputs[0].is(Items.MUSHROOM_STEW)
                        && drained != null && drained.itemOutputs[0].is(Items.BOWL)
                        && drained.fluidOutputs[0].isFluidStackIdentical(soupFluid),
                "The canner fills and drains vanilla bowls at the original 1000 mB");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void bottleAndEmptyEveryAddonDrink(GameTestHelper helper) {
        var canner = MachineRecipeMaps.Canner;
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            var id = entry.getKey().location();
            if (!(entry.getValue() instanceof BottledDrinkItem drink)
                    || !id.getNamespace().equals(GregTechFood.MOD_ID)) {
                continue;
            }
            var filled = canner.findRecipe(List.of(new ItemStack(Items.GLASS_BOTTLE)),
                    List.of(fluid(id.toString(), 250)), true, 1, 4);
            helper.assertTrue(filled != null && filled.itemOutputs.length == 1
                    && filled.itemOutputs[0].is(drink), "Bottle result for " + id);
            var emptied = canner.findRecipe(List.of(filled.itemOutputs[0]), List.of(), true, 1, 4);
            helper.assertTrue(emptied != null && emptied.itemOutputs[0].is(Items.GLASS_BOTTLE)
                    && emptied.fluidOutputs[0].isFluidStackIdentical(fluid(id.toString(), 250)), "Drain result for " + id);
        }
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void drinkOneServingFromGTCup(GameTestHelper helper) {
        var player = helper.makeMockPlayer();
        player.getFoodData().setFoodLevel(14);
        ItemStack cup = stack("gt6ce:fluid_cup");
        var tank = FluidUtil.getFluidHandler(cup).orElseThrow(() -> new IllegalStateException("Cup lacks a fluid tank"));
        helper.assertTrue(tank.fill(fluid("gt6food:coffee", 200), FluidAction.EXECUTE) == 200, "Fill a partial cup");
        player.setItemInHand(InteractionHand.MAIN_HAND, tank.getContainer());
        var partial = new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(!MinecraftForge.EVENT_BUS.post(partial), "A partial cup cannot start drinking");

        helper.assertTrue(tank.fill(fluid("gt6food:coffee", 50), FluidAction.EXECUTE) == 50, "Fill a complete serving");
        var start = new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(MinecraftForge.EVENT_BUS.post(start) && player.isUsingItem(), "Start drinking from the cup");
        var held = player.getMainHandItem();
        var finish = new LivingEntityUseItemEvent.Finish(player, held.copy(), 0, held);
        MinecraftForge.EVENT_BUS.post(finish);
        player.stopUsingItem();
        helper.assertTrue(FluidUtil.getFluidContained(finish.getResultStack()).isEmpty()
                && player.getFoodData().getFoodLevel() > 14, "The cup loses exactly one serving and feeds the player");
        helper.succeed();
    }

    @GameTest(template = "test_empty")
    public static void migrateLegacyFoodLoot(GameTestHelper helper) {
        ItemStack old = stack("gt6ce:raw_ham");
        old.setCount(2);
        old.setHoverName(Component.literal("Smoked ham"));
        var converted = MigratedFoodLoot.migrate(old);
        helper.assertTrue(converted.is(stack("gt6food:raw_ham").getItem()) && converted.getCount() == 2
                && converted.getHoverName().equals(old.getHoverName()), "Preserve count and name when migrating loot");
        helper.assertTrue(MigratedFoodLoot.migrate(new ItemStack(Items.IRON_INGOT)).is(Items.IRON_INGOT),
                "Ordinary loot stays in the original registry");
        helper.succeed();
    }

    private static FluidStack produce(GameTestHelper helper, RecipeMap map, String result, int amount,
                                      List<ItemStack> items, FluidStack... fluids) {
        var recipe = map.findRecipe(items, List.of(fluids), true, items.size(), 4);
        var expected = fluid("gt6food:" + result, amount);
        helper.assertTrue(recipe != null && recipe.fluidOutputs.length == 1
                && recipe.fluidOutputs[0].isFluidStackIdentical(expected), "Expected machine output for " + result);
        return recipe.fluidOutputs[0];
    }

    private static ItemStack stack(String id) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id)), id));
    }

    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(id)), id), amount);
    }

    private DrinkMigrationGameTests() {}
}
